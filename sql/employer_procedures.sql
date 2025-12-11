-- ============================================
-- All Employer-Related Stored Procedures
-- ============================================

USE JobBoard;
GO

-- ============================================
-- 1. Add Skill to Job (JobSkills table)
-- ============================================
IF OBJECT_ID('employer.sp_AddJobSkill', 'P') IS NOT NULL
    DROP PROCEDURE employer.sp_AddJobSkill;
GO

CREATE PROCEDURE employer.sp_AddJobSkill
    @JobId INT,
    @SkillId INT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Check if the relationship already exists
    IF NOT EXISTS (SELECT 1 FROM employer.JobSkills WHERE JobId = @JobId AND SkillId = @SkillId)
    BEGIN
        INSERT INTO employer.JobSkills (JobId, SkillId)
        VALUES (@JobId, @SkillId);
    END
END;
GO

-- ============================================
-- 2. Get Total Published Job Count
-- ============================================
IF OBJECT_ID('employer.sp_GetTotalPublishedJobCount', 'P') IS NOT NULL
    DROP PROCEDURE employer.sp_GetTotalPublishedJobCount;
GO

CREATE PROCEDURE employer.sp_GetTotalPublishedJobCount
    @TotalCount INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT @TotalCount = COUNT(*)
    FROM employer.Jobs
    WHERE StatusId = 2 -- Published
      AND ExpiresAt > GETDATE();
END;
GO

-- ============================================
-- 3. Search Jobs with Filters
-- ============================================
IF OBJECT_ID('employer.sp_SearchJobs', 'P') IS NOT NULL
    DROP PROCEDURE employer.sp_SearchJobs;
GO

CREATE PROCEDURE employer.sp_SearchJobs
    @Keyword NVARCHAR(250) = NULL,
    @CityId INT = NULL,
    @SkillId INT = NULL,
    @PageNumber INT = 1,
    @PageSize INT = 20
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @Offset INT = (@PageNumber - 1) * @PageSize;
    
    SELECT 
        j.JobId,
        j.Title,
        j.Slug,
        j.CompanyId,
        c.Name AS CompanyName,
        c.LogoUrl,
        ci.Name AS CityName,
        j.SalaryMin,
        j.SalaryMax,
        j.Currency,
        j.IsFeatured,
        STUFF((
            SELECT ', ' + s.Name
            FROM employer.JobSkills js
            INNER JOIN common.Skills s ON js.SkillId = s.SkillId
            WHERE js.JobId = j.JobId
            FOR XML PATH(''), TYPE
        ).value('.', 'NVARCHAR(MAX)'), 1, 2, '') AS Skills
    FROM employer.Jobs j
    INNER JOIN employer.Companies c ON j.CompanyId = c.CompanyId
    LEFT JOIN common.Cities ci ON j.CityId = ci.CityId
    WHERE j.StatusId = 2 -- Published
      AND j.ExpiresAt > GETDATE()
      AND (@Keyword IS NULL OR j.Title LIKE '%' + @Keyword + '%' OR j.Description LIKE '%' + @Keyword + '%')
      AND (@CityId IS NULL OR j.CityId = @CityId)
      AND (@SkillId IS NULL OR EXISTS (
          SELECT 1 FROM employer.JobSkills js 
          WHERE js.JobId = j.JobId AND js.SkillId = @SkillId
      ))
    ORDER BY j.IsFeatured DESC, j.PostedAt DESC
    OFFSET @Offset ROWS
    FETCH NEXT @PageSize ROWS ONLY;
END;
GO

-- ============================================
-- 4. Get Job Detail
-- ============================================
IF OBJECT_ID('employer.sp_GetJobDetail', 'P') IS NOT NULL
    DROP PROCEDURE employer.sp_GetJobDetail;
GO

CREATE PROCEDURE employer.sp_GetJobDetail
    @JobId INT
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        j.JobId,
        j.Title,
        j.Slug,
        j.CompanyId,
        c.Name AS CompanyName,
        c.LogoUrl,
        c.Website,
        c.Description AS CompanyDescription,
        ci.Name AS CityName,
        co.Name AS CountryName,
        j.SalaryMin,
        j.SalaryMax,
        j.Currency,
        j.Description,
        j.Requirements,
        j.Benefits,
        et.Name AS EmploymentType,
        sl.Name AS SeniorityLevel,
        rt.Name AS RemoteType,
        j.IsFeatured,
        j.PostedAt,
        j.ExpiresAt,
        j.ViewsCount,
        j.ApplicationsCount
    FROM employer.Jobs j
    INNER JOIN employer.Companies c ON j.CompanyId = c.CompanyId
    LEFT JOIN common.Cities ci ON j.CityId = ci.CityId
    LEFT JOIN common.Countries co ON ci.CountryId = co.CountryId
    LEFT JOIN common.EmploymentTypes et ON j.EmploymentTypeId = et.EmploymentTypeId
    LEFT JOIN common.SeniorityLevels sl ON j.SeniorityLevelId = sl.SeniorityLevelId
    LEFT JOIN common.RemoteTypes rt ON j.RemoteTypeId = rt.RemoteTypeId
    WHERE j.JobId = @JobId;
END;
GO

-- ============================================
-- 5. Get Related Jobs (Same Company or Skills)
-- ============================================
IF OBJECT_ID('employer.sp_GetRelatedJobs', 'P') IS NOT NULL
    DROP PROCEDURE employer.sp_GetRelatedJobs;
GO

CREATE PROCEDURE employer.sp_GetRelatedJobs
    @JobId INT,
    @CompanyId INT,
    @Limit INT = 5
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT TOP (@Limit)
        j.JobId,
        j.Title,
        j.Slug,
        c.Name AS CompanyName,
        ci.Name AS CityName,
        j.SalaryMin,
        j.SalaryMax,
        j.Currency,
        j.PostedAt
    FROM employer.Jobs j
    INNER JOIN employer.Companies c ON j.CompanyId = c.CompanyId
    LEFT JOIN common.Cities ci ON j.CityId = ci.CityId
    WHERE j.JobId != @JobId
      AND j.StatusId = 2 -- Published
      AND j.ExpiresAt > GETDATE()
      AND (
          j.CompanyId = @CompanyId
          OR EXISTS (
              SELECT 1 
              FROM employer.JobSkills js1
              INNER JOIN employer.JobSkills js2 ON js1.SkillId = js2.SkillId
              WHERE js1.JobId = @JobId AND js2.JobId = j.JobId
          )
      )
    ORDER BY 
        CASE WHEN j.CompanyId = @CompanyId THEN 1 ELSE 2 END,
        j.PostedAt DESC;
END;
GO

-- ============================================
-- 6. Get Top Employers
-- ============================================
IF OBJECT_ID('employer.sp_GetTopEmployers', 'P') IS NOT NULL
    DROP PROCEDURE employer.sp_GetTopEmployers;
GO

CREATE PROCEDURE employer.sp_GetTopEmployers
    @Limit INT = 10
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT TOP (@Limit)
        c.CompanyId,
        c.Name,
        c.ShortName,
        c.LogoUrl,
        c.Website,
        c.IsVerified,
        COUNT(DISTINCT j.JobId) AS ActiveJobsCount,
        ci.Name AS CityName
    FROM employer.Companies c
    LEFT JOIN employer.Jobs j ON c.CompanyId = j.CompanyId 
        AND j.StatusId = 2 
        AND j.ExpiresAt > GETDATE()
    LEFT JOIN common.Cities ci ON c.HeadquartersCityId = ci.CityId
    WHERE c.IsVerified = 1
    GROUP BY 
        c.CompanyId, c.Name, c.ShortName, c.LogoUrl, 
        c.Website, c.IsVerified, ci.Name
    ORDER BY ActiveJobsCount DESC, c.Name;
END;
GO

-- ============================================
-- 7. Get Company Detail
-- ============================================
IF OBJECT_ID('employer.sp_GetCompanyDetail', 'P') IS NOT NULL
    DROP PROCEDURE employer.sp_GetCompanyDetail;
GO

CREATE PROCEDURE employer.sp_GetCompanyDetail
    @CompanyId INT
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        c.CompanyId,
        c.Name,
        c.ShortName,
        c.LogoUrl,
        c.CoverImageUrl,
        c.Website,
        c.Description,
        c.EmployeeCount,
        c.FoundedYear,
        c.Industry,
        c.IsVerified,
        ci.Name AS CityName,
        co.Name AS CountryName,
        (SELECT COUNT(*) FROM employer.Jobs WHERE CompanyId = c.CompanyId AND StatusId = 2 AND ExpiresAt > GETDATE()) AS ActiveJobsCount
    FROM employer.Companies c
    LEFT JOIN common.Cities ci ON c.HeadquartersCityId = ci.CityId
    LEFT JOIN common.Countries co ON ci.CountryId = co.CountryId
    WHERE c.CompanyId = @CompanyId;
END;
GO

-- ============================================
-- 8. Get Recruiter Dashboard Stats
-- ============================================
IF OBJECT_ID('employer.sp_GetRecruiterDashboardStats', 'P') IS NOT NULL
    DROP PROCEDURE employer.sp_GetRecruiterDashboardStats;
GO

CREATE PROCEDURE employer.sp_GetRecruiterDashboardStats
    @RecruiterId INT
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        COUNT(DISTINCT j.JobId) AS TotalJobs,
        COUNT(DISTINCT CASE WHEN j.StatusId = 2 AND j.ExpiresAt > GETDATE() THEN j.JobId END) AS ActiveJobs,
        ISNULL(SUM(j.ApplicationsCount), 0) AS TotalApplications,
        ISNULL(SUM(CASE WHEN j.PostedAt >= DATEADD(DAY, -7, GETDATE()) THEN j.ApplicationsCount ELSE 0 END), 0) AS NewApplications,
        0 AS InterviewsScheduled, -- Placeholder if you have interview tracking
        ISNULL(SUM(j.ViewsCount), 0) AS TotalViews
    FROM employer.Jobs j
    WHERE j.RecruiterId = @RecruiterId;
END;
GO

-- ============================================
-- 9. Get Recruiter Jobs (Paginated)
-- ============================================
IF OBJECT_ID('employer.sp_GetRecruiterJobs', 'P') IS NOT NULL
    DROP PROCEDURE employer.sp_GetRecruiterJobs;
GO

CREATE PROCEDURE employer.sp_GetRecruiterJobs
    @RecruiterId INT,
    @PageNumber INT = 1,
    @PageSize INT = 10
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @Offset INT = (@PageNumber - 1) * @PageSize;
    
    SELECT 
        j.JobId,
        j.Title,
        j.Slug,
        js.Name AS Status,
        j.StatusId,
        j.PostedAt,
        j.ExpiresAt,
        j.ViewsCount,
        j.ApplicationsCount,
        j.IsFeatured,
        ci.Name AS CityName,
        et.Name AS EmploymentType
    FROM employer.Jobs j
    LEFT JOIN common.Cities ci ON j.CityId = ci.CityId
    LEFT JOIN common.EmploymentTypes et ON j.EmploymentTypeId = et.EmploymentTypeId
    LEFT JOIN common.JobStatuses js ON j.StatusId = js.StatusId
    WHERE j.RecruiterId = @RecruiterId
    ORDER BY j.PostedAt DESC
    OFFSET @Offset ROWS
    FETCH NEXT @PageSize ROWS ONLY;
END;
GO

-- ============================================
-- 10. Get Recent Applications by Recruiter
-- ============================================
IF OBJECT_ID('employer.sp_GetRecentApplicationsByRecruiter', 'P') IS NOT NULL
    DROP PROCEDURE employer.sp_GetRecentApplicationsByRecruiter;
GO

CREATE PROCEDURE employer.sp_GetRecentApplicationsByRecruiter
    @RecruiterId INT,
    @Limit INT = 10
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT TOP (@Limit)
        a.ApplicationId,
        a.JobId,
        j.Title AS JobTitle,
        a.CandidateId,
        can.FullName AS CandidateName,
        a.AppliedAt,
        a.StatusId,
        ast.Name AS Status,
        a.ResumeUrl,
        a.CoverLetter
    FROM candidate.Applications a
    INNER JOIN employer.Jobs j ON a.JobId = j.JobId
    INNER JOIN candidate.Candidates can ON a.CandidateId = can.CandidateId
    LEFT JOIN candidate.ApplicationStatuses ast ON a.StatusId = ast.StatusId
    WHERE j.RecruiterId = @RecruiterId
    ORDER BY a.AppliedAt DESC;
END;
GO

-- ============================================
-- 11. Get Application Stats by Status
-- ============================================
IF OBJECT_ID('employer.sp_GetApplicationStatsByStatus', 'P') IS NOT NULL
    DROP PROCEDURE employer.sp_GetApplicationStatsByStatus;
GO

CREATE PROCEDURE employer.sp_GetApplicationStatsByStatus
    @RecruiterId INT
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT 
        ISNULL(ast.Name, 'Unknown') AS Status,
        COUNT(a.ApplicationId) AS Count
    FROM candidate.Applications a
    INNER JOIN employer.Jobs j ON a.JobId = j.JobId
    LEFT JOIN candidate.ApplicationStatuses ast ON a.StatusId = ast.StatusId
    WHERE j.RecruiterId = @RecruiterId
    GROUP BY ast.Name
    ORDER BY COUNT(a.ApplicationId) DESC;
END;
GO

-- ============================================
-- 12. Create Recruiter Profile
-- ============================================
IF OBJECT_ID('employer.sp_CreateRecruiter', 'P') IS NOT NULL
    DROP PROCEDURE employer.sp_CreateRecruiter;
GO

CREATE PROCEDURE employer.sp_CreateRecruiter
    @UserId INT,
    @CompanyId INT,
    @Title NVARCHAR(200),
    @IsPrimaryContact BIT = 0,
    @RecruiterId INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    
    INSERT INTO employer.Recruiters (
        UserId,
        CompanyId,
        Title,
        IsPrimaryContact
    )
    VALUES (
        @UserId,
        @CompanyId,
        @Title,
        @IsPrimaryContact
    );
    
    SET @RecruiterId = SCOPE_IDENTITY();
END;
GO

-- ============================================
-- Success Message
-- ============================================
PRINT 'All employer stored procedures created successfully!';
GO
