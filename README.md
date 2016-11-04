Extract published resources using the ArchivesSpace API.
Generate FindingAid item objects in DSpace.

## Generic Operations
These generic operations are command line operations that may be application to any institution using ArchivesSpace.

### Get Published Resources

#### Input
* ASObject Export Property File (described above)
* repositoryId - repository to query
* resourceExcludes - comma separated list of resource ids to exclude (optional)
* modificationDate - search for resources modified after a specific date (optional)

#### Ouptut
* JSON Array of resource metadata - listing all published resources (modified after modification date) in the repository excluding those in the exclusion list

### Get Resource EAD

#### Input
* ASObject Export Property File (described above)
* repositoryId - repository to query
* resourceId - resource to extract
* output file - file name to use for exported resource

#### Output
* PDF output file

### TBD: Get other object types
As needs arise, code will be created to extract other object types such as Digital Objects and Accessions.  

***

## Specific Workflows Built on Generic Operations
These operations are specific to the way the Georgetown University Library synchronizes content between ArchivesSpace and DSpace.  These operations would likely require modification if adopted by other institutions.

### DSpace Finding Aid Inventory File
A CSV file listing the finding aids in DSpace.  Note that DSpace does not store a bitstream update date.

* Collection Handle
* DSpace Item Handle
* DSpace Item Title (commas removed)
* Finding Aid URL
* Bitstream name 
* Bitstream last modification date (optional, from ead name)

[Sample DSpace Query](SampleDSpaceQuery.sql)

### Create Resource Metadata

This workflow is intended to generate a Bulk Metadata Edit Spreadsheet for new resources in ArchivesSpace that are not yet in DSpace.

Georgetown University also had a need to remove old full text EAD's (created by Archivist Toolkit) from DSpace.  

#### Input
* ASObject Export Property File (described above)
* repositoryIds - comma separated list of repository ids to query (or blank to query all)
* DG Finding Aid Inventory File (described above)

#### Output
* DSpace Metadata Report (repository id, resource id, resource title)
* Bulk Metadata Ingest CSV (See https://wiki.duraspace.org/display/DSDOC5x/Batch+Metadata+Editing)
* DSpace Itemupdate folders (for full-text deletion)

```
/.../itemupdate (path defined in the property file)
    /{resource-id}
      dublin_core.xml - metadata created with a crosswalk from the AS Resource Object JSON
```

#### DSpace Update 
```
  dspace itemupdate -e {eperson} -s ${ITEMUPDATEDIR} -D ORIGINAL_AND_DERIVATIVES -i dc.relation.uri
```

### Create Resource Ingest 

#### Input
* ASObject Export Property File (described above)
* repositoryIds - comma separated list of repository ids to query (or blank to query all)
* DG Finding Aid Inventory File (described above)

#### Output
* DSpace Item Ingest Report (repository id, resource id, resource title)
* DSpace Item Ingest folders

```
/.../as-ingest (path defined in the property file)
  /{dg-collection-handle}
    /{resource-id}
      resource-ead.pdf
      contents
      dublin_core.xml - metadata created with a crosswalk from the AS Resource Object JSON
```

#### DSpace Update
```
dpsace import -a -e {eperson} -c {dg-collection-handle} -s /.../as-ingest/{dg-collection-handle} -m /.../mapfile/{dg-collection-handle}-{YYYY-MM-DD}.txt
```

### Create Resource Update

#### Input
* ASObject Export Property File (described above)
* repositoryIds - comma separated list of repository ids to query (or blank to query all)
* DG Finding Aid Inventory File (described above)
* modification date - find items modified since a particular date

#### Output
* DSpace Item Update Report (repository id, resource id, resource title)
* DSpace Item Update folders

```
/.../as-update (path defined in the property file)
    /{resource-id}
        resource-ead.pdf
        dublin_core.xml - contains dc.relation.uri for the source item (used to itentify the object to update)
```

#### DSpace Update
```
for REPODIR in ${UPDATEDIR}
do
  dspace itemupdate -e {eperson} -s ${REPODIR} -D ORIGINAL_AND_DERIVATIVES -i dc.relation.uri
  dspace itemupdate -e {eperson} -s ${REPODIR} -A -i dc.relation.uri
done
```

***
[![Georgetown University Library IT Code Repositories](https://raw.githubusercontent.com/Georgetown-University-Libraries/georgetown-university-libraries.github.io/master/LIT-logo-small.png)Georgetown University Library IT Code Repositories](http://georgetown-university-libraries.github.io/)

