-- ============================================
-- Registration Support Stored Procedures
-- ============================================

USE JobBoard;
GO

-- 1. Create Candidate Profile after User Registration
IF OBJECT_ID('candidate.sp_CreateCandidateProfile', 'P') IS NOT NULL
    DROP PROCEDURE candidate.sp_CreateCandidateProfile;
GO

CREATE PROCEDURE candidate.sp_CreateCandidateProfile
    @UserId INT,
    @FullName NVARCHAR(250),
    @CandidateId INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO candidate.Candidates (
        UserId,
        FullName,
        PublicProfile,
        CreatedAt
    )
    VALUES (
        @UserId,
        @FullName,
        1, -- Public by default
        GETDATE()
    );

    SET @CandidateId = SCOPE_IDENTITY();
END;
GO

-- 2. Create Recruiter Profile after User Registration
IF OBJECT_ID('employer.sp_CreateRecruiterProfile', 'P') IS NOT NULL
    DROP PROCEDURE employer.sp_CreateRecruiterProfile;
GO

CREATE PROCEDURE employer.sp_CreateRecruiterProfile
    @UserId INT,
    @CompanyName NVARCHAR(300),
    @RecruiterTitle NVARCHAR(200),
    @CompanyId INT OUTPUT,
    @RecruiterId INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    BEGIN TRANSACTION;
    
    BEGIN TRY
        -- Check if company with this name already exists
        SELECT @CompanyId = CompanyId 
        FROM employer.Companies 
        WHERE Name = @CompanyName;
        
        -- If company doesn't exist, create it
        IF @CompanyId IS NULL
        BEGIN
            INSERT INTO employer.Companies (
                Name,
                IsVerified,
                CreatedAt
            )
            VALUES (
                @CompanyName,
                0, -- Not verified initially
                GETDATE()
            );
            
            SET @CompanyId = SCOPE_IDENTITY();
        END
        
        -- Create recruiter profile
        INSERT INTO employer.Recruiters (
            UserId,
            CompanyId,
            Title,
            IsPrimaryContact
        )
        VALUES (
            @UserId,
            @CompanyId,
            @RecruiterTitle,
            1 -- First recruiter is primary contact
        );
        
        SET @RecruiterId = SCOPE_IDENTITY();
        
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

-- 3. Get Recruiter Profile by UserId (for Login)
IF OBJECT_ID('employer.sp_GetRecruiterByUserId', 'P') IS NOT NULL
    DROP PROCEDURE employer.sp_GetRecruiterByUserId;
GO

CREATE PROCEDURE employer.sp_GetRecruiterByUserId
    @UserId INT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        RecruiterId,
        UserId,
        CompanyId,
        Title,
        IsPrimaryContact
    FROM employer.Recruiters
    WHERE UserId = @UserId;
END;
GO

-- 4. Get Candidate by User ID
IF OBJECT_ID('candidate.sp_GetCandidateByUserId', 'P') IS NOT NULL
    DROP PROCEDURE candidate.sp_GetCandidateByUserId;
GO

CREATE PROCEDURE candidate.sp_GetCandidateByUserId
    @UserId INT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        CandidateId,
        UserId,
        FullName,
        Headline,
        Summary,
        YearsOfExperience,
        CityId,
        CountryId,
        AvatarUrl,
        PublicProfile,
        CreatedAt,
        UpdatedAt
    FROM candidate.Candidates
    WHERE UserId = @UserId;
END;
GO

-- 4. Complete User Registration (Combined User + Profile Creation)
IF OBJECT_ID('auth.sp_RegisterUser', 'P') IS NOT NULL
    DROP PROCEDURE auth.sp_RegisterUser;
GO

CREATE PROCEDURE auth.sp_RegisterUser
    @Email NVARCHAR(255),
    @PasswordHash VARBINARY(256),
    @Salt VARBINARY(16),
    @Role NVARCHAR(50),
    @FullName NVARCHAR(250),
    @CompanyName NVARCHAR(300) = NULL,
    @RecruiterTitle NVARCHAR(200) = NULL,
    @UserId INT OUTPUT,
    @ProfileId INT OUTPUT,
    @CompanyId INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    BEGIN TRANSACTION;
    
    BEGIN TRY
        -- Create user account
        INSERT INTO auth.Users (Email, PasswordHash, Salt, Role, IsEmailConfirmed, IsActive, CreatedAt)
        VALUES (@Email, @PasswordHash, @Salt, @Role, 0, 1, GETDATE());
        
        SET @UserId = SCOPE_IDENTITY();
        
        -- Create profile based on role
        IF @Role = 'Candidate'
        BEGIN
            DECLARE @CandidateId INT;
            
            INSERT INTO candidate.Candidates (UserId, FullName, PublicProfile, CreatedAt)
            VALUES (@UserId, @FullName, 1, GETDATE());
            
            SET @ProfileId = SCOPE_IDENTITY();
            SET @CompanyId = NULL;
        END
        ELSE IF @Role = 'Recruiter'
        BEGIN
            DECLARE @RecruiterId INT;
            
            -- Check if company exists
            SELECT @CompanyId = CompanyId 
            FROM employer.Companies 
            WHERE Name = @CompanyName;
            
            -- Create company if not exists
            IF @CompanyId IS NULL
            BEGIN
                INSERT INTO employer.Companies (Name, IsVerified, CreatedAt)
                VALUES (@CompanyName, 0, GETDATE());
                
                SET @CompanyId = SCOPE_IDENTITY();
            END
            
            -- Create recruiter profile
            INSERT INTO employer.Recruiters (UserId, CompanyId, Title, IsPrimaryContact)
            VALUES (@UserId, @CompanyId, ISNULL(@RecruiterTitle, 'Recruiter'), 1);
            
            SET @ProfileId = SCOPE_IDENTITY();
        END
        
        COMMIT TRANSACTION;
        
        SELECT 1 AS Success;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        DECLARE @ErrorSeverity INT = ERROR_SEVERITY();
        DECLARE @ErrorState INT = ERROR_STATE();
        
        RAISERROR(@ErrorMessage, @ErrorSeverity, @ErrorState);
    END CATCH
END;
GO

PRINT 'Registration stored procedures created successfully!';
GO
