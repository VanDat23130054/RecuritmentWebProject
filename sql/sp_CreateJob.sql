-- ============================================
-- Stored Procedure: employer.sp_CreateJob
-- Description: Creates a new job posting
-- ============================================

USE JobBoard;
GO

IF OBJECT_ID('employer.sp_CreateJob', 'P') IS NOT NULL
    DROP PROCEDURE employer.sp_CreateJob;
GO

CREATE PROCEDURE employer.sp_CreateJob
    @CompanyId INT,
    @Title NVARCHAR(250),
    @Description NVARCHAR(MAX),
    @Requirements NVARCHAR(MAX) = NULL,
    @Benefits NVARCHAR(MAX) = NULL,
    @CityId INT,
    @EmploymentTypeId TINYINT,
    @SeniorityLevelId TINYINT = NULL,
    @RemoteTypeId TINYINT = NULL,
    @SalaryMin INT = NULL,
    @SalaryMax INT = NULL,
    @Currency VARCHAR(3) = 'USD',
    @ExpiresAt VARCHAR(50) = NULL,
    @StatusId TINYINT = 1,
    @JobId INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @PostedAt DATETIME2 = GETDATE();
    DECLARE @ExpiresAtDate DATETIME2;
    DECLARE @RecruiterId INT;
    
    BEGIN TRY
        BEGIN TRANSACTION;
        
        -- Get recruiter ID for the company (primary contact preferred)
        SELECT TOP 1 @RecruiterId = RecruiterId
        FROM employer.Recruiters
        WHERE CompanyId = @CompanyId
        ORDER BY IsPrimaryContact DESC, RecruiterId ASC;
        
        IF @RecruiterId IS NULL
        BEGIN
            RAISERROR('No recruiter found for this company', 16, 1);
            RETURN;
        END
        
        -- Parse ExpiresAt string to datetime (format: YYYY-MM-DD)
        IF @ExpiresAt IS NOT NULL AND @ExpiresAt != ''
        BEGIN
            SET @ExpiresAtDate = CAST(@ExpiresAt AS DATETIME2);
        END
        ELSE
        BEGIN
            -- Default expiration: 30 days from now
            SET @ExpiresAtDate = DATEADD(DAY, 30, @PostedAt);
        END
        
        -- Insert job posting
        INSERT INTO employer.Jobs (
            CompanyId,
            RecruiterId,
            Title,
            Description,
            Requirements,
            Benefits,
            CityId,
            EmploymentTypeId,
            SeniorityLevelId,
            RemoteTypeId,
            SalaryMin,
            SalaryMax,
            Currency,
            StatusId,
            ApplicationsCount,
            ViewsCount,
            IsFeatured,
            PostedAt,
            ExpiresAt,
            UpdatedAt
        )
        VALUES (
            @CompanyId,
            @RecruiterId,
            @Title,
            @Description,
            @Requirements,
            @Benefits,
            @CityId,
            @EmploymentTypeId,
            @SeniorityLevelId,
            @RemoteTypeId,
            @SalaryMin,
            @SalaryMax,
            @Currency,
            @StatusId,
            0,                              -- ApplicationsCount starts at 0
            0,                              -- ViewsCount starts at 0
            0,                              -- IsFeatured defaults to false
            @PostedAt,
            @ExpiresAtDate,
            @PostedAt
        );
        
        -- Get the newly created JobId
        SET @JobId = SCOPE_IDENTITY();
        
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
        
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        DECLARE @ErrorSeverity INT = ERROR_SEVERITY();
        DECLARE @ErrorState INT = ERROR_STATE();
        
        RAISERROR(@ErrorMessage, @ErrorSeverity, @ErrorState);
    END CATCH
END;
GO

-- ============================================
-- Test the procedure (optional - comment out for production)
-- ============================================
/*
DECLARE @NewJobId INT;

EXEC employer.sp_CreateJob
    @CompanyId = 1,
    @Title = 'Senior Software Engineer',
    @Description = 'We are looking for an experienced software engineer...',
    @Requirements = 'Bachelor degree in Computer Science, 5+ years experience',
    @Benefits = 'Health insurance, 401k, remote work',
    @CityId = 1,
    @EmploymentTypeId = 1,      -- Full-time
    @SeniorityLevelId = 3,      -- Senior
    @RemoteTypeId = 2,          -- Hybrid
    @SalaryMin = 80000,
    @SalaryMax = 120000,
    @Currency = 'USD',
    @ExpiresAt = '2025-02-10',
    @StatusId = 2,              -- Published
    @JobId = @NewJobId OUTPUT;

SELECT @NewJobId AS NewJobId;
SELECT * FROM employer.Jobs WHERE JobId = @NewJobId;
*/
