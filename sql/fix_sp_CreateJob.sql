-- ============================================
-- Fix sp_CreateJob to use RecruiterId parameter
-- ============================================

USE JobBoard;
GO

IF OBJECT_ID('employer.sp_CreateJob', 'P') IS NOT NULL
    DROP PROCEDURE employer.sp_CreateJob;
GO

CREATE PROCEDURE employer.sp_CreateJob
    @CompanyId INT,
    @RecruiterId INT,
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
    
    BEGIN TRY
        BEGIN TRANSACTION;
        
        -- Verify recruiter belongs to the company
        IF NOT EXISTS (
            SELECT 1 FROM employer.Recruiters 
            WHERE RecruiterId = @RecruiterId AND CompanyId = @CompanyId
        )
        BEGIN
            RAISERROR('Recruiter does not belong to this company', 16, 1);
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

PRINT 'sp_CreateJob updated successfully to use RecruiterId parameter!';
GO
