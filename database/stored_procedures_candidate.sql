-- =====================================================
-- Stored Procedures for Candidate Schema
-- =====================================================

-- =====================================================
-- sp_GetCandidateByUserId
-- Get candidate profile by User ID
-- =====================================================
IF OBJECT_ID('candidate.sp_GetCandidateByUserId', 'P') IS NOT NULL
    DROP PROCEDURE candidate.sp_GetCandidateByUserId;
GO

CREATE PROCEDURE candidate.sp_GetCandidateByUserId
    @UserId INT
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        c.CandidateId, 
        c.UserId, 
        c.FullName, 
        c.Headline, 
        c.Summary, 
        c.YearsOfExperience,
        c.CityId, 
        c.CountryId, 
        c.AvatarUrl, 
        c.PublicProfile, 
        c.CreatedAt, 
        c.UpdatedAt,
        ISNULL(ci.Name, '') AS CityName, 
        ISNULL(co.Name, '') AS CountryName
    FROM candidate.Candidates c
    LEFT JOIN common.Cities ci ON c.CityId = ci.CityId
    LEFT JOIN common.Countries co ON c.CountryId = co.CountryId
    WHERE c.UserId = @UserId;
END
GO

-- =====================================================
-- sp_UpdateCandidateProfile
-- Update candidate profile by User ID
-- =====================================================
IF OBJECT_ID('candidate.sp_UpdateCandidateProfile', 'P') IS NOT NULL
    DROP PROCEDURE candidate.sp_UpdateCandidateProfile;
GO

CREATE PROCEDURE candidate.sp_UpdateCandidateProfile
    @UserId INT,
    @FullName NVARCHAR(200),
    @Headline NVARCHAR(500),
    @Summary NVARCHAR(MAX),
    @YearsOfExperience FLOAT = NULL,
    @CityId INT = NULL,
    @CountryId INT = NULL,
    @AvatarUrl NVARCHAR(500) = NULL,
    @PublicProfile BIT = 0,
    @Success BIT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    UPDATE candidate.Candidates 
    SET 
        FullName = @FullName, 
        Headline = @Headline, 
        Summary = @Summary, 
        YearsOfExperience = @YearsOfExperience, 
        CityID = @CityId, 
        CountryID = @CountryId, 
        AvatarUrl = @AvatarUrl, 
        PublicProfile = @PublicProfile, 
        UpdatedAt = GETDATE()
    WHERE UserID = @UserId;
    
    SET @Success = CASE WHEN @@ROWCOUNT > 0 THEN 1 ELSE 0 END;
END
GO

-- =====================================================
-- sp_GetApplicationsByCandidate
-- Get applications for a candidate with optional status filter and pagination
-- =====================================================
IF OBJECT_ID('candidate.sp_GetApplicationsByCandidate', 'P') IS NOT NULL
    DROP PROCEDURE candidate.sp_GetApplicationsByCandidate;
GO

CREATE PROCEDURE candidate.sp_GetApplicationsByCandidate
    @CandidateId INT,
    @Status NVARCHAR(50) = NULL,
    @PageNumber INT = 1,
    @PageSize INT = 10
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @Offset INT = (@PageNumber - 1) * @PageSize;
    
    SELECT 
        a.ApplicationId, 
        a.JobId, 
        j.Title AS JobTitle, 
        a.CandidateId, 
        c.FullName AS CandidateName, 
        u.Email AS CandidateEmail, 
        e.Name AS CompanyName, 
        a.CoverLetter, 
        a.Source, 
        a.AppliedAt, 
        a.Status, 
        a.ResumeId, 
        r.FileUrl, 
        r.FileName
    FROM candidate.Applications a
    LEFT JOIN candidate.Candidates c ON a.CandidateId = c.CandidateId
    LEFT JOIN auth.Users u ON c.UserID = u.UserID
    LEFT JOIN employer.Jobs j ON a.JobId = j.JobId
    LEFT JOIN employer.Companies e ON j.CompanyID = e.CompanyID
    LEFT JOIN candidate.Resumes r ON a.ResumeId = r.ResumeId
    WHERE a.CandidateId = @CandidateId
      AND (@Status IS NULL OR @Status = '' OR a.Status = @Status)
    ORDER BY a.AppliedAt DESC
    OFFSET @Offset ROWS FETCH NEXT @PageSize ROWS ONLY;
END
GO

-- =====================================================
-- sp_GetApplicationCountByCandidate
-- Get total application count for a candidate (for pagination)
-- =====================================================
IF OBJECT_ID('candidate.sp_GetApplicationCountByCandidate', 'P') IS NOT NULL
    DROP PROCEDURE candidate.sp_GetApplicationCountByCandidate;
GO

CREATE PROCEDURE candidate.sp_GetApplicationCountByCandidate
    @CandidateId INT,
    @Status NVARCHAR(50) = NULL,
    @Count INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT @Count = COUNT(*) 
    FROM candidate.Applications a 
    WHERE a.CandidateId = @CandidateId
      AND (@Status IS NULL OR @Status = '' OR a.Status = @Status);
END
GO

-- =====================================================
-- sp_WithdrawApplication
-- Allow candidate to withdraw an application
-- =====================================================
IF OBJECT_ID('candidate.sp_WithdrawApplication', 'P') IS NOT NULL
    DROP PROCEDURE candidate.sp_WithdrawApplication;
GO

CREATE PROCEDURE candidate.sp_WithdrawApplication
    @ApplicationId INT,
    @CandidateId INT,
    @Success BIT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Allow withdrawal if status is Applied or Under Review (case-insensitive, trimmed)
    UPDATE candidate.Applications 
    SET Status = 'Withdrawn' 
    WHERE ApplicationId = @ApplicationId 
      AND CandidateId = @CandidateId 
      AND LTRIM(RTRIM(LOWER(Status))) IN ('applied', 'under review');
    
    SET @Success = CASE WHEN @@ROWCOUNT > 0 THEN 1 ELSE 0 END;
END
GO

-- =====================================================
-- sp_HasApplied
-- Check if a candidate has already applied for a job
-- =====================================================
IF OBJECT_ID('candidate.sp_HasApplied', 'P') IS NOT NULL
    DROP PROCEDURE candidate.sp_HasApplied;
GO

CREATE PROCEDURE candidate.sp_HasApplied
    @CandidateId INT,
    @JobId INT,
    @HasApplied BIT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @Count INT;
    
    SELECT @Count = COUNT(*) 
    FROM candidate.Applications 
    WHERE CandidateId = @CandidateId 
      AND JobId = @JobId;
    
    SET @HasApplied = CASE WHEN @Count > 0 THEN 1 ELSE 0 END;
END
GO

-- =====================================================
-- sp_SaveResume
-- Save a new resume to the database
-- =====================================================
IF OBJECT_ID('candidate.sp_SaveResume', 'P') IS NOT NULL
    DROP PROCEDURE candidate.sp_SaveResume;
GO

CREATE PROCEDURE candidate.sp_SaveResume
    @CandidateId INT,
    @DriveFileId NVARCHAR(500),
    @FileName NVARCHAR(500),
    @FileUrl NVARCHAR(1000),
    @ResumeId INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    INSERT INTO candidate.Resumes (CandidateId, DriveFileId, FileName, FileUrl, IsPrimary, IsPublic, UploadedAt)
    VALUES (@CandidateId, @DriveFileId, @FileName, @FileUrl, 1, 1, GETDATE());
    
    SET @ResumeId = SCOPE_IDENTITY();
END
GO

-- =====================================================
-- sp_GetResumeById
-- Get resume by resume ID
-- =====================================================
IF OBJECT_ID('candidate.sp_GetResumeById', 'P') IS NOT NULL
    DROP PROCEDURE candidate.sp_GetResumeById;
GO

CREATE PROCEDURE candidate.sp_GetResumeById
    @ResumeId INT
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        ResumeId, 
        CandidateId, 
        DriveFileId, 
        FileName, 
        FileUrl, 
        ParsedJson,
        IsPrimary, 
        IsPublic, 
        UploadedAt 
    FROM candidate.Resumes 
    WHERE ResumeId = @ResumeId;
END
GO

-- =====================================================
-- sp_GetResumesByCandidateId
-- Get all resumes for a candidate
-- =====================================================
IF OBJECT_ID('candidate.sp_GetResumesByCandidateId', 'P') IS NOT NULL
    DROP PROCEDURE candidate.sp_GetResumesByCandidateId;
GO

CREATE PROCEDURE candidate.sp_GetResumesByCandidateId
    @CandidateId INT
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        ResumeId, 
        CandidateId, 
        DriveFileId, 
        FileName, 
        FileUrl, 
        ParsedJson,
        IsPrimary, 
        IsPublic, 
        UploadedAt 
    FROM candidate.Resumes 
    WHERE CandidateId = @CandidateId
    ORDER BY IsPrimary DESC, UploadedAt DESC;
END
GO

-- =====================================================
-- sp_UpdateResumeDriveFileId
-- Update Google Drive file ID for an existing resume
-- =====================================================
IF OBJECT_ID('candidate.sp_UpdateResumeDriveFileId', 'P') IS NOT NULL
    DROP PROCEDURE candidate.sp_UpdateResumeDriveFileId;
GO

CREATE PROCEDURE candidate.sp_UpdateResumeDriveFileId
    @ResumeId INT,
    @DriveFileId NVARCHAR(500),
    @FileUrl NVARCHAR(1000)
AS
BEGIN
    SET NOCOUNT ON;
    
    UPDATE candidate.Resumes 
    SET DriveFileId = @DriveFileId, FileUrl = @FileUrl 
    WHERE ResumeId = @ResumeId;
END
GO

-- =====================================================
-- sp_DeleteResume
-- Delete resume record from database
-- =====================================================
IF OBJECT_ID('candidate.sp_DeleteResume', 'P') IS NOT NULL
    DROP PROCEDURE candidate.sp_DeleteResume;
GO

CREATE PROCEDURE candidate.sp_DeleteResume
    @ResumeId INT
AS
BEGIN
    SET NOCOUNT ON;
    
    DELETE FROM candidate.Resumes WHERE ResumeId = @ResumeId;
END
GO

-- =====================================================
-- sp_GetAllResumes
-- Get all resumes (for migration purposes)
-- =====================================================
IF OBJECT_ID('candidate.sp_GetAllResumes', 'P') IS NOT NULL
    DROP PROCEDURE candidate.sp_GetAllResumes;
GO

CREATE PROCEDURE candidate.sp_GetAllResumes
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        ResumeId, 
        CandidateId, 
        DriveFileId, 
        FileName, 
        FileUrl, 
        ParsedJson,
        IsPrimary, 
        IsPublic, 
        UploadedAt 
    FROM candidate.Resumes;
END
GO

-- =====================================================
-- sp_SetPrimaryResume
-- Set a resume as primary for a candidate
-- =====================================================
IF OBJECT_ID('candidate.sp_SetPrimaryResume', 'P') IS NOT NULL
    DROP PROCEDURE candidate.sp_SetPrimaryResume;
GO

CREATE PROCEDURE candidate.sp_SetPrimaryResume
    @ResumeId INT,
    @CandidateId INT
AS
BEGIN
    SET NOCOUNT ON;
    
    BEGIN TRANSACTION;
    
    -- First, set all resumes for this candidate as non-primary
    UPDATE candidate.Resumes 
    SET IsPrimary = 0 
    WHERE CandidateId = @CandidateId;
    
    -- Then set the specified resume as primary
    UPDATE candidate.Resumes 
    SET IsPrimary = 1 
    WHERE ResumeId = @ResumeId AND CandidateId = @CandidateId;
    
    COMMIT TRANSACTION;
END
GO

-- =====================================================
-- sp_RenameResume
-- Rename a resume file (update display name)
-- =====================================================
IF OBJECT_ID('candidate.sp_RenameResume', 'P') IS NOT NULL
    DROP PROCEDURE candidate.sp_RenameResume;
GO

CREATE PROCEDURE candidate.sp_RenameResume
    @ResumeId INT,
    @NewFileName NVARCHAR(500),
    @CandidateId INT,
    @Success BIT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    UPDATE candidate.Resumes 
    SET FileName = @NewFileName 
    WHERE ResumeId = @ResumeId AND CandidateId = @CandidateId;
    
    SET @Success = CASE WHEN @@ROWCOUNT > 0 THEN 1 ELSE 0 END;
END
GO

-- =====================================================
-- sp_DeleteResumeSecure
-- Delete a resume with candidate ownership check
-- =====================================================
IF OBJECT_ID('candidate.sp_DeleteResumeSecure', 'P') IS NOT NULL
    DROP PROCEDURE candidate.sp_DeleteResumeSecure;
GO

CREATE PROCEDURE candidate.sp_DeleteResumeSecure
    @ResumeId INT,
    @CandidateId INT,
    @Success BIT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    DELETE FROM candidate.Resumes 
    WHERE ResumeId = @ResumeId AND CandidateId = @CandidateId;
    
    SET @Success = CASE WHEN @@ROWCOUNT > 0 THEN 1 ELSE 0 END;
END
GO

-- =====================================================
-- sp_GetApplicationDetailByCandidate
-- Get application detail for a candidate (ownership check)
-- =====================================================
IF OBJECT_ID('candidate.sp_GetApplicationDetailByCandidate', 'P') IS NOT NULL
    DROP PROCEDURE candidate.sp_GetApplicationDetailByCandidate;
GO

CREATE PROCEDURE candidate.sp_GetApplicationDetailByCandidate
    @ApplicationId INT,
    @CandidateId INT
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        a.ApplicationId,
        a.JobId,
        j.Title AS JobTitle,
        c.Name AS CompanyName,
        a.AppliedAt,
        a.Status,
        a.CoverLetter,
        r.FileName AS ResumeFileName,
        r.FileUrl AS ResumeFileUrl,
        a.RecruiterNote
    FROM candidate.Applications a
    LEFT JOIN employer.Jobs j ON a.JobId = j.JobId
    LEFT JOIN employer.Companies c ON j.CompanyID = c.CompanyID
    LEFT JOIN candidate.Resumes r ON a.ResumeId = r.ResumeId
    WHERE a.ApplicationId = @ApplicationId AND a.CandidateId = @CandidateId;
END
GO

-- =====================================================
-- sp_UpdateApplicationCoverLetter
-- Update application cover letter (only if status is 'Applied')
-- =====================================================
IF OBJECT_ID('candidate.sp_UpdateApplicationCoverLetter', 'P') IS NOT NULL
    DROP PROCEDURE candidate.sp_UpdateApplicationCoverLetter;
GO

CREATE PROCEDURE candidate.sp_UpdateApplicationCoverLetter
    @ApplicationId INT,
    @CandidateId INT,
    @CoverLetter NVARCHAR(MAX),
    @Success BIT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    UPDATE candidate.Applications 
    SET CoverLetter = @CoverLetter 
    WHERE ApplicationId = @ApplicationId 
      AND CandidateId = @CandidateId 
      AND Status = 'Applied';
    
    SET @Success = CASE WHEN @@ROWCOUNT > 0 THEN 1 ELSE 0 END;
END
GO

PRINT 'All candidate stored procedures created successfully.';
GO
