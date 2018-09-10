package edu.georgetown.library.asExport;

public enum ResourceStatus {
    NoLongerExistsInArchivesSpace,
    NoLongerPublishedInArchivesSpace,
    Unpublished,
    Unparsed,
    Parsed,
    ExportFailure,
    Skipped,
    MetadataCreated,
    IngestFolderCreated,
    FullTextUpdateFolderCreated;
}
