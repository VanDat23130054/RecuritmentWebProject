-- SQL Stored Procedures for Application Management Feature
-- Database: JobBoard
-- Schema: candidate (Applications table), employer (supporting tables)
-- Author: Generated for RecruiterWebProject
-- Date: December 2025
-- Based on actual jobboard_v3.sql schema

USE JobBoard;
GO

-- =============================================
-- Procedure 1: Get Applications by Recruiter with Filters
-- =============================================
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_GetApplicationsByRecruiter')
    DROP PROCEDURE employer.sp_GetApplicationsByRecruiter;
GO

CREATE PROCEDURE employer.sp_GetApplicationsByRecruiter
    @recruiterId INT,
    @jobId INT = NULL,
    @status NVARCHAR(50) = NULL,
    @keyword NVARCHAR(100) = NULL,
    @pageNumber INT = 1,
    @pageSize INT = 20
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @offset INT = (@pageNumber - 1) * @pageSize;
    
    SELECT 
        a.ApplicationId,
        a.JobId,
        a.CandidateId,
        a.CoverLetter,
        a.Source,
        a.AppliedAt,
        a.Status,
        a.ResumeId,
        j.Title AS jobTitle,
        c.Name AS companyName,
        cand.FullName AS candidateName,
        u.Email AS candidateEmail,
        res.FileUrl,
        res.FileName
    FROM candidate.Applications a
    INNER JOIN employer.Jobs j ON a.JobId = j.JobId
    INNER JOIN employer.Recruiters r ON j.RecruiterId = r.RecruiterId
    INNER JOIN employer.Companies c ON j.CompanyId = c.CompanyId
    INNER JOIN candidate.Candidates cand ON a.CandidateId = cand.CandidateId
    INNER JOIN auth.Users u ON cand.UserId = u.UserId
    LEFT JOIN candidate.Resumes res ON a.ResumeId = res.ResumeId
    WHERE r.RecruiterId = @recruiterId
        AND (@jobId IS NULL OR a.JobId = @jobId)
        AND (@status IS NULL OR a.Status = @status)
        AND (@keyword IS NULL OR 
             cand.FullName LIKE '%' + @keyword + '%' OR 
             u.Email LIKE '%' + @keyword + '%')
    ORDER BY a.AppliedAt DESC
    OFFSET @offset ROWS
    FETCH NEXT @pageSize ROWS ONLY;
END;
GO

-- =============================================
-- Procedure 2: Get Application Count by Recruiter
-- =============================================
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_GetApplicationCountByRecruiter')
    DROP PROCEDURE employer.sp_GetApplicationCountByRecruiter;
GO

CREATE PROCEDURE employer.sp_GetApplicationCountByRecruiter
    @recruiterId INT,
    @jobId INT = NULL,
    @status NVARCHAR(50) = NULL,
    @keyword NVARCHAR(100) = NULL,
    @totalCount INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT @totalCount = COUNT(*)
    FROM candidate.Applications a
    INNER JOIN employer.Jobs j ON a.JobId = j.JobId
    INNER JOIN employer.Recruiters r ON j.RecruiterId = r.RecruiterId
    INNER JOIN candidate.Candidates cand ON a.CandidateId = cand.CandidateId
    INNER JOIN auth.Users u ON cand.UserId = u.UserId
    WHERE r.RecruiterId = @recruiterId
        AND (@jobId IS NULL OR a.JobId = @jobId)
        AND (@status IS NULL OR a.Status = @status)
        AND (@keyword IS NULL OR 
             cand.FullName LIKE '%' + @keyword + '%' OR 
             u.Email LIKE '%' + @keyword + '%');
END;
GO

-- =============================================
-- Procedure 3: Get Application Detail with Authorization
-- =============================================
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_GetApplicationDetail')
    DROP PROCEDURE employer.sp_GetApplicationDetail;
GO

CREATE PROCEDURE employer.sp_GetApplicationDetail
    @applicationId INT,
    @recruiterId INT
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        a.ApplicationId,
        a.JobId,
        a.CandidateId,
        a.CoverLetter,
        a.AppliedAt,
        a.Status,
        a.ResumeId,
        a.Source,
        j.Title AS jobTitle,
        j.Description AS jobDescription,
        c.Name AS companyName,
        cand.FullName AS candidateName,
        u.Email AS candidateEmail,
        cand.Summary AS candidatePhone,
        cand.CityId AS candidateLocation,
        res.FileName AS resumeFileName,
        res.FileUrl AS resumeFileUrl
    FROM candidate.Applications a
    INNER JOIN employer.Jobs j ON a.JobId = j.JobId
    INNER JOIN employer.Recruiters r ON j.RecruiterId = r.RecruiterId
    INNER JOIN employer.Companies c ON j.CompanyId = c.CompanyId
    INNER JOIN candidate.Candidates cand ON a.CandidateId = cand.CandidateId
    INNER JOIN auth.Users u ON cand.UserId = u.UserId
    LEFT JOIN candidate.Resumes res ON a.ResumeId = res.ResumeId
    WHERE a.ApplicationId = @applicationId
        AND r.RecruiterId = @recruiterId;
END;
GO

-- =============================================
-- Procedure 4: Update Application Status with Authorization
-- =============================================
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_UpdateApplicationStatus')
    DROP PROCEDURE employer.sp_UpdateApplicationStatus;
GO

CREATE PROCEDURE employer.sp_UpdateApplicationStatus
    @applicationId INT,
    @recruiterId INT,
    @newStatus NVARCHAR(50),
    @success BIT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    SET @success = 0;
    
    BEGIN TRY
        BEGIN TRANSACTION;
        
        -- Check if recruiter owns the job
        IF EXISTS (
            SELECT 1 
            FROM candidate.Applications a
            INNER JOIN employer.Jobs j ON a.JobId = j.JobId
            WHERE a.ApplicationId = @applicationId
                AND j.RecruiterId = @recruiterId
        )
        BEGIN
            -- Note: Applications table doesn't have updatedAt column in schema
            UPDATE candidate.Applications
            SET Status = @newStatus
            WHERE ApplicationId = @applicationId;
            
            SET @success = 1;
        END
        
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
        
        SET @success = 0;
        THROW;
    END CATCH
END;
GO

-- =============================================
-- Procedure 5: Get Application Status Counts by Recruiter
-- =============================================
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_GetApplicationStatusCounts')
    DROP PROCEDURE employer.sp_GetApplicationStatusCounts;
GO

CREATE PROCEDURE employer.sp_GetApplicationStatusCounts
    @recruiterId INT
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        COUNT(*) AS allCount,
        SUM(CASE WHEN a.Status = 'Applied' THEN 1 ELSE 0 END) AS appliedCount,
        SUM(CASE WHEN a.Status = 'Under Review' THEN 1 ELSE 0 END) AS underReviewCount,
        SUM(CASE WHEN a.Status = 'Interview Scheduled' THEN 1 ELSE 0 END) AS interviewCount,
        SUM(CASE WHEN a.Status = 'Rejected' THEN 1 ELSE 0 END) AS rejectedCount
    FROM candidate.Applications a
    INNER JOIN employer.Jobs j ON a.JobId = j.JobId
    INNER JOIN employer.Recruiters r ON j.RecruiterId = r.RecruiterId
    WHERE r.RecruiterId = @recruiterId;
END;
GO

-- =============================================
-- Test Queries
-- =============================================

-- Test 1: Get all applications for recruiter 2 (Alice from ACME Corp)
-- EXEC employer.sp_GetApplicationsByRecruiter @recruiterId = 2, @pageNumber = 1, @pageSize = 20;

-- Test 2: Get application count
-- DECLARE @count INT;
-- EXEC employer.sp_GetApplicationCountByRecruiter @recruiterId = 2, @totalCount = @count OUTPUT;
-- SELECT @count AS TotalApplications;

-- Test 3: Get application detail (Application 1 for Job 1 which belongs to Recruiter 2)
-- EXEC employer.sp_GetApplicationDetail @applicationId = 1, @recruiterId = 2;

-- Test 4: Update application status
-- DECLARE @success BIT;
-- EXEC employer.sp_UpdateApplicationStatus @applicationId = 1, @recruiterId = 2, @newStatus = 'Under Review', @success = @success OUTPUT;
-- SELECT @success AS UpdateSuccess;

-- Test 5: Get status counts
-- EXEC employer.sp_GetApplicationStatusCounts @recruiterId = 2;

-- =============================================
-- Schema Notes
-- =============================================
-- Applications table: candidate.Applications (NOT employer.Applications)
-- Column names use PascalCase: ApplicationId, JobId, Status, AppliedAt, etc.
-- Candidates.FullName is a single field (not firstName + lastName)
-- Applications table does NOT have updatedAt column
-- Resumes: candidate.Resumes with FileName, FileUrl, ResumeData (NVARCHAR(MAX))

PRINT 'All stored procedures created successfully!';
