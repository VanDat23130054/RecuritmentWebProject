-- Chat Feature Database Schema
-- Schema: system

-- =============================================
-- Create Conversations Table
-- =============================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Conversations' AND schema_id = SCHEMA_ID('system'))
BEGIN
    CREATE TABLE system.Conversations (
        ConversationId INT IDENTITY(1,1) PRIMARY KEY,
        CandidateId INT NOT NULL,
        RecruiterId INT NOT NULL,
        JobId INT NULL, -- Optional: link conversation to a specific job
        CreatedAt DATETIME2 DEFAULT GETDATE(),
        LastMessageAt DATETIME2 DEFAULT GETDATE(),
        IsActive BIT DEFAULT 1,
        
        CONSTRAINT FK_Conversations_Candidate FOREIGN KEY (CandidateId) 
            REFERENCES candidate.Candidates(CandidateId),
        CONSTRAINT FK_Conversations_Recruiter FOREIGN KEY (RecruiterId) 
            REFERENCES employer.Recruiters(RecruiterId),
        CONSTRAINT FK_Conversations_Job FOREIGN KEY (JobId) 
            REFERENCES employer.Jobs(JobId),
        CONSTRAINT UQ_Conversation_Participants UNIQUE (CandidateId, RecruiterId, JobId)
    );
END
GO

-- =============================================
-- Create Messages Table
-- =============================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Messages' AND schema_id = SCHEMA_ID('system'))
BEGIN
    CREATE TABLE system.Messages (
        MessageId BIGINT IDENTITY(1,1) PRIMARY KEY,
        ConversationId INT NOT NULL,
        SenderId INT NOT NULL, -- UserId from auth.Users
        Body NVARCHAR(MAX) NOT NULL,
        SentAt DATETIME2 DEFAULT GETDATE(),
        IsRead BIT DEFAULT 0,
        ReadAt DATETIME2 NULL,
        
        CONSTRAINT FK_Messages_Conversation FOREIGN KEY (ConversationId) 
            REFERENCES system.Conversations(ConversationId) ON DELETE CASCADE,
        CONSTRAINT FK_Messages_Sender FOREIGN KEY (SenderId) 
            REFERENCES auth.Users(UserId)
    );
END
GO

-- Index for faster message retrieval
CREATE INDEX IX_Messages_ConversationId ON system.Messages(ConversationId, SentAt DESC);
CREATE INDEX IX_Messages_SenderId ON system.Messages(SenderId);
GO

-- =============================================
-- Stored Procedure: Get or Create Conversation
-- =============================================
CREATE OR ALTER PROCEDURE system.sp_GetOrCreateConversation
    @CandidateId INT,
    @RecruiterId INT,
    @JobId INT = NULL
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @ConversationId INT;
    
    -- Try to find existing conversation
    SELECT @ConversationId = ConversationId 
    FROM system.Conversations 
    WHERE CandidateId = @CandidateId 
      AND RecruiterId = @RecruiterId 
      AND ((@JobId IS NULL AND JobId IS NULL) OR JobId = @JobId);
    
    -- If not found, create new conversation
    IF @ConversationId IS NULL
    BEGIN
        INSERT INTO system.Conversations (CandidateId, RecruiterId, JobId)
        VALUES (@CandidateId, @RecruiterId, @JobId);
        
        SET @ConversationId = SCOPE_IDENTITY();
    END
    
    -- Return conversation with participant details
    SELECT 
        c.ConversationId,
        c.CandidateId,
        c.RecruiterId,
        c.JobId,
        c.CreatedAt,
        c.LastMessageAt,
        c.IsActive,
        -- Candidate info
        cand.FullName AS CandidateName,
        cand.AvatarUrl AS CandidateAvatar,
        candUser.Email AS CandidateEmail,
        -- Recruiter info (get from auth.Users)
        recUser.Email AS RecruiterEmail,
        -- Company info
        comp.Name AS CompanyName,
        comp.LogoUrl AS CompanyLogo,
        -- Job info (if linked)
        j.Title AS JobTitle
    FROM system.Conversations c
    INNER JOIN candidate.Candidates cand ON c.CandidateId = cand.CandidateId
    INNER JOIN auth.Users candUser ON cand.UserId = candUser.UserId
    INNER JOIN employer.Recruiters r ON c.RecruiterId = r.RecruiterId
    INNER JOIN auth.Users recUser ON r.UserId = recUser.UserId
    INNER JOIN employer.Companies comp ON r.CompanyId = comp.CompanyId
    LEFT JOIN employer.Jobs j ON c.JobId = j.JobId
    WHERE c.ConversationId = @ConversationId;
END
GO

-- =============================================
-- Stored Procedure: Get User Conversations
-- =============================================
CREATE OR ALTER PROCEDURE system.sp_GetUserConversations
    @UserId INT,
    @UserRole NVARCHAR(50) -- 'Candidate' or 'Recruiter'
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        c.ConversationId,
        c.CandidateId,
        c.RecruiterId,
        c.JobId,
        c.CreatedAt,
        c.LastMessageAt,
        c.IsActive,
        -- Candidate info
        cand.FullName AS CandidateName,
        cand.AvatarUrl AS CandidateAvatar,
        candUser.Email AS CandidateEmail,
        -- Recruiter info (get from auth.Users)
        recUser.Email AS RecruiterEmail,
        -- Company info
        comp.Name AS CompanyName,
        comp.LogoUrl AS CompanyLogo,
        -- Job info
        j.Title AS JobTitle,
        -- Last message preview
        (SELECT TOP 1 Body FROM system.Messages m 
         WHERE m.ConversationId = c.ConversationId 
         ORDER BY m.SentAt DESC) AS LastMessage,
        -- Unread count for current user
        (SELECT COUNT(*) FROM system.Messages m 
         WHERE m.ConversationId = c.ConversationId 
           AND m.SenderId != @UserId 
           AND m.IsRead = 0) AS UnreadCount
    FROM system.Conversations c
    INNER JOIN candidate.Candidates cand ON c.CandidateId = cand.CandidateId
    INNER JOIN auth.Users candUser ON cand.UserId = candUser.UserId
    INNER JOIN employer.Recruiters r ON c.RecruiterId = r.RecruiterId
    INNER JOIN auth.Users recUser ON r.UserId = recUser.UserId
    INNER JOIN employer.Companies comp ON r.CompanyId = comp.CompanyId
    LEFT JOIN employer.Jobs j ON c.JobId = j.JobId
    WHERE c.IsActive = 1
      AND (
          (@UserRole = 'Candidate' AND candUser.UserId = @UserId)
          OR (@UserRole IN ('Recruiter', 'EmployerAdmin') AND recUser.UserId = @UserId)
      )
    ORDER BY c.LastMessageAt DESC;
END
GO

-- =============================================
-- Stored Procedure: Get Conversation Messages
-- =============================================
CREATE OR ALTER PROCEDURE system.sp_GetConversationMessages
    @ConversationId INT,
    @PageNumber INT = 1,
    @PageSize INT = 50
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        m.MessageId,
        m.ConversationId,
        m.SenderId,
        m.Body,
        m.SentAt,
        m.IsRead,
        m.ReadAt,
        u.Email AS SenderEmail,
        COALESCE(cand.FullName, u.Email) AS SenderName,
        cand.AvatarUrl AS SenderAvatar,
        u.Role AS SenderRole
    FROM system.Messages m
    INNER JOIN auth.Users u ON m.SenderId = u.UserId
    LEFT JOIN candidate.Candidates cand ON u.UserId = cand.UserId
    WHERE m.ConversationId = @ConversationId
    ORDER BY m.SentAt ASC
    OFFSET (@PageNumber - 1) * @PageSize ROWS
    FETCH NEXT @PageSize ROWS ONLY;
END
GO

-- =============================================
-- Stored Procedure: Send Message
-- =============================================
CREATE OR ALTER PROCEDURE system.sp_SendMessage
    @ConversationId INT,
    @SenderId INT,
    @Body NVARCHAR(MAX)
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Insert the message
    INSERT INTO system.Messages (ConversationId, SenderId, Body)
    VALUES (@ConversationId, @SenderId, @Body);
    
    DECLARE @MessageId BIGINT = SCOPE_IDENTITY();
    
    -- Update conversation's last message time
    UPDATE system.Conversations 
    SET LastMessageAt = GETDATE() 
    WHERE ConversationId = @ConversationId;
    
    -- Return the new message with sender details
    SELECT 
        m.MessageId,
        m.ConversationId,
        m.SenderId,
        m.Body,
        m.SentAt,
        m.IsRead,
        u.Email AS SenderEmail,
        COALESCE(cand.FullName, u.Email) AS SenderName,
        cand.AvatarUrl AS SenderAvatar,
        u.Role AS SenderRole
    FROM system.Messages m
    INNER JOIN auth.Users u ON m.SenderId = u.UserId
    LEFT JOIN candidate.Candidates cand ON u.UserId = cand.UserId
    WHERE m.MessageId = @MessageId;
END
GO

-- =============================================
-- Stored Procedure: Mark Messages as Read
-- =============================================
CREATE OR ALTER PROCEDURE system.sp_MarkMessagesAsRead
    @ConversationId INT,
    @UserId INT
AS
BEGIN
    SET NOCOUNT ON;
    
    UPDATE system.Messages 
    SET IsRead = 1, ReadAt = GETDATE()
    WHERE ConversationId = @ConversationId 
      AND SenderId != @UserId 
      AND IsRead = 0;
    
    SELECT @@ROWCOUNT AS MessagesMarkedRead;
END
GO

-- =============================================
-- Stored Procedure: Get Unread Message Count
-- =============================================
CREATE OR ALTER PROCEDURE system.sp_GetUnreadMessageCount
    @UserId INT,
    @UserRole NVARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT COUNT(*) AS UnreadCount
    FROM system.Messages m
    INNER JOIN system.Conversations c ON m.ConversationId = c.ConversationId
    INNER JOIN candidate.Candidates cand ON c.CandidateId = cand.CandidateId
    INNER JOIN auth.Users candUser ON cand.UserId = candUser.UserId
    INNER JOIN employer.Recruiters r ON c.RecruiterId = r.RecruiterId
    INNER JOIN auth.Users recUser ON r.UserId = recUser.UserId
    WHERE m.SenderId != @UserId
      AND m.IsRead = 0
      AND c.IsActive = 1
      AND (
          (@UserRole = 'Candidate' AND candUser.UserId = @UserId)
          OR (@UserRole IN ('Recruiter', 'EmployerAdmin') AND recUser.UserId = @UserId)
      );
END
GO

-- =============================================
-- Stored Procedure: Get Conversation by ID
-- =============================================
CREATE OR ALTER PROCEDURE system.sp_GetConversationById
    @ConversationId INT
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        c.ConversationId,
        c.CandidateId,
        c.RecruiterId,
        c.JobId,
        c.CreatedAt,
        c.LastMessageAt,
        c.IsActive,
        -- Candidate info
        cand.FullName AS CandidateName,
        cand.AvatarUrl AS CandidateAvatar,
        candUser.UserId AS CandidateUserId,
        candUser.Email AS CandidateEmail,
        -- Recruiter info (get from auth.Users)
        recUser.UserId AS RecruiterUserId,
        recUser.Email AS RecruiterEmail,
        -- Company info
        comp.Name AS CompanyName,
        comp.LogoUrl AS CompanyLogo,
        -- Job info
        j.Title AS JobTitle
    FROM system.Conversations c
    INNER JOIN candidate.Candidates cand ON c.CandidateId = cand.CandidateId
    INNER JOIN auth.Users candUser ON cand.UserId = candUser.UserId
    INNER JOIN employer.Recruiters r ON c.RecruiterId = r.RecruiterId
    INNER JOIN auth.Users recUser ON r.UserId = recUser.UserId
    INNER JOIN employer.Companies comp ON r.CompanyId = comp.CompanyId
    LEFT JOIN employer.Jobs j ON c.JobId = j.JobId
    WHERE c.ConversationId = @ConversationId;
END
GO

-- =============================================
-- Stored Procedure: Start Conversation from Application
-- =============================================
CREATE OR ALTER PROCEDURE system.sp_StartConversationFromApplication
    @ApplicationId INT,
    @InitialMessage NVARCHAR(MAX) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @CandidateId INT, @JobId INT, @RecruiterId INT, @ConversationId INT;
    
    -- Get application details
    SELECT 
        @CandidateId = a.CandidateId,
        @JobId = a.JobId,
        @RecruiterId = j.RecruiterId
    FROM candidate.Applications a
    INNER JOIN employer.Jobs j ON a.JobId = j.JobId
    WHERE a.ApplicationId = @ApplicationId;
    
    IF @CandidateId IS NULL OR @RecruiterId IS NULL
    BEGIN
        RAISERROR('Application not found or invalid', 16, 1);
        RETURN;
    END
    
    -- Get or create conversation
    EXEC system.sp_GetOrCreateConversation 
        @CandidateId = @CandidateId,
        @RecruiterId = @RecruiterId,
        @JobId = @JobId;
END
GO
