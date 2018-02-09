package edu.georgetown.library.asExport;

public enum ResourceStatus {
    NoLongerExistsInArchivesSpace,
    Unpublished,
    Unparsed,
    Parsed,
    ExportFailure,
    Skipped,
    MetadataCreated,
    IngestFolderCreated,
    FullTextUpdateFolderCreated;
}
