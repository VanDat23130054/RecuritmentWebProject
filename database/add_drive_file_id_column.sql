-- Add DriveFileId column to candidate.Resumes table for Google Drive integration
-- This column will store the unique file ID from Google Drive

USE RecruitmentDB;
GO

-- Add the DriveFileId column
ALTER TABLE candidate.Resumes 
ADD DriveFileId NVARCHAR(255) NULL;
GO

-- Create index for faster lookups by Drive file ID
CREATE INDEX idx_resumes_driveFileId 
ON candidate.Resumes(DriveFileId);
GO

-- Add comment to document the column (SQL Server 2012+)
EXEC sys.sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'Google Drive file ID for cloud-stored resume', 
    @level0type = N'SCHEMA', @level0name = 'candidate',
    @level1type = N'TABLE', @level1name = 'Resumes',
    @level2type = N'COLUMN', @level2name = 'DriveFileId';
GO

-- Verify the column was added
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    CHARACTER_MAXIMUM_LENGTH,
    IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'candidate'
  AND TABLE_NAME = 'Resumes'
  AND COLUMN_NAME = 'DriveFileId';
GO

PRINT 'DriveFileId column added successfully to candidate.Resumes table';
