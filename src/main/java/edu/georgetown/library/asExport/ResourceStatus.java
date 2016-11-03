package edu.georgetown.library.asExport;

public enum ResourceStatus {
    Unpublished,
    Unparsed,
    Parsed,
    ExportFailure,
    Skipped,
    MetadataCreated,
    IngestFolderCreated,
    FullTextUpdateFolderCreated;
}
