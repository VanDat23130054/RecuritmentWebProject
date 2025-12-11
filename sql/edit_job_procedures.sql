-- ============================================
-- Edit Job Stored Procedures
-- ============================================

USE JobBoard;
GO

-- ============================================
-- 1. Get Job For Edit (includes all details needed for edit form)
-- ============================================
IF OBJECT_ID('employer.sp_GetJobForEdit', 'P') IS NOT NULL
    DROP PROCEDURE employer.sp_GetJobForEdit;
GO

CREATE PROCEDURE employer.sp_GetJobForEdit
    @JobId INT,
    @RecruiterId INT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Verify the job belongs to the recruiter before returning data
    IF EXISTS (
        SELECT 1 FROM employer.Jobs 
        WHERE JobId = @JobId AND RecruiterId = @RecruiterId
    )
    BEGIN
        SELECT 
            j.JobId,
            j.CompanyId,
            j.RecruiterId,
            j.Title,
            j.Slug,
            j.Description,
            j.Requirements,
            j.Benefits,
            j.CityId,
            j.EmploymentTypeId,
            j.SeniorityLevelId,
            j.RemoteTypeId,
            j.SalaryMin,
            j.SalaryMax,
            j.Currency,
            j.StatusId,
            j.IsFeatured,
            j.PostedAt,
            j.ExpiresAt,
            j.ViewsCount,
            j.ApplicationsCount
        FROM employer.Jobs j
        WHERE j.JobId = @JobId;
        
        -- Also return the associated skills
        SELECT 
            js.SkillId,
            s.Name AS SkillName
        FROM employer.JobSkills js
        INNER JOIN common.Skills s ON js.SkillId = s.SkillId
        WHERE js.JobId = @JobId;
    END
    ELSE
    BEGIN
        -- Return empty result if not authorized
        SELECT NULL AS JobId WHERE 1 = 0;
        SELECT NULL AS SkillId WHERE 1 = 0;
    END
END;
GO

-- ============================================
-- 2. Update Job
-- ============================================
IF OBJECT_ID('employer.sp_UpdateJob', 'P') IS NOT NULL
    DROP PROCEDURE employer.sp_UpdateJob;
GO

CREATE PROCEDURE employer.sp_UpdateJob
    @JobId INT,
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
    @StatusId TINYINT
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @ExpiresAtDate DATETIME2;
    DECLARE @Success BIT = 0;
    
    BEGIN TRY
        BEGIN TRANSACTION;
        
        -- Verify the job belongs to the recruiter
        IF NOT EXISTS (
            SELECT 1 FROM employer.Jobs 
            WHERE JobId = @JobId AND RecruiterId = @RecruiterId
        )
        BEGIN
            RAISERROR('Job not found or you do not have permission to edit it', 16, 1);
            RETURN;
        END
        
        -- Parse ExpiresAt string to datetime (format: YYYY-MM-DD)
        IF @ExpiresAt IS NOT NULL AND @ExpiresAt != ''
        BEGIN
            SET @ExpiresAtDate = CAST(@ExpiresAt AS DATETIME2);
        END
        ELSE
        BEGIN
            -- Keep existing expiration or set default
            SELECT @ExpiresAtDate = ISNULL(ExpiresAt, DATEADD(DAY, 30, GETDATE()))
            FROM employer.Jobs
            WHERE JobId = @JobId;
        END
        
        -- Update job posting
        UPDATE employer.Jobs
        SET 
            Title = @Title,
            Description = @Description,
            Requirements = @Requirements,
            Benefits = @Benefits,
            CityId = @CityId,
            EmploymentTypeId = @EmploymentTypeId,
            SeniorityLevelId = @SeniorityLevelId,
            RemoteTypeId = @RemoteTypeId,
            SalaryMin = @SalaryMin,
            SalaryMax = @SalaryMax,
            Currency = @Currency,
            StatusId = @StatusId,
            ExpiresAt = @ExpiresAtDate,
            UpdatedAt = GETDATE()
        WHERE JobId = @JobId AND RecruiterId = @RecruiterId;
        
        SET @Success = 1;
        COMMIT TRANSACTION;
        
        SELECT @Success AS Success;
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
-- 3. Remove Job Skill (for updating skills during edit)
-- ============================================
IF OBJECT_ID('employer.sp_RemoveJobSkill', 'P') IS NOT NULL
    DROP PROCEDURE employer.sp_RemoveJobSkill;
GO

CREATE PROCEDURE employer.sp_RemoveJobSkill
    @JobId INT,
    @SkillId INT
AS
BEGIN
    SET NOCOUNT ON;
    
    DELETE FROM employer.JobSkills
    WHERE JobId = @JobId AND SkillId = @SkillId;
END;
GO

-- ============================================
-- 4. Remove All Job Skills (for complete skill replacement)
-- ============================================
IF OBJECT_ID('employer.sp_RemoveAllJobSkills', 'P') IS NOT NULL
    DROP PROCEDURE employer.sp_RemoveAllJobSkills;
GO

CREATE PROCEDURE employer.sp_RemoveAllJobSkills
    @JobId INT
AS
BEGIN
    SET NOCOUNT ON;
    
    DELETE FROM employer.JobSkills
    WHERE JobId = @JobId;
END;
GO

-- ============================================
-- 5. Delete Job (soft delete by changing status)
-- ============================================
IF OBJECT_ID('employer.sp_DeleteJob', 'P') IS NOT NULL
    DROP PROCEDURE employer.sp_DeleteJob;
GO

CREATE PROCEDURE employer.sp_DeleteJob
    @JobId INT,
    @RecruiterId INT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Verify the job belongs to the recruiter
    IF EXISTS (
        SELECT 1 FROM employer.Jobs 
        WHERE JobId = @JobId AND RecruiterId = @RecruiterId
    )
    BEGIN
        -- Set status to Draft (1) or you could create a Deleted status
        UPDATE employer.Jobs
        SET StatusId = 1, -- Draft
            UpdatedAt = GETDATE()
        WHERE JobId = @JobId;
        
        SELECT 1 AS Success;
    END
    ELSE
    BEGIN
        SELECT 0 AS Success;
    END
END;
GO

PRINT 'Edit job stored procedures created successfully!';
GO
