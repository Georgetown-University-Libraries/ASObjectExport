Extract published resources using the ArchivesSpace API.
Generate FindingAid item objects in DSpace.

## ASObject Export Property File Syntax
```
service: http://<your-server>:8089/
user: <read-only-user-id>
password: <user's password>

#List of repository ids to query
repositories: 1,3,5
# DSpace handle to map resources to (one per repository above)
handle_1: 12345/6789 
handle_3: 12345/6790
handle_5: 12345/6791
```
***

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
A CSV file listing the finding aids in DSpace.

* Collection Handle
* DSpace Item Handle
* DSpace Item Title
* Finding Aid URL
* Bitstream last modification date (optional)

### Create Resource Ingest 

#### Input
* ASObject Export Property File (described above)
* repositoryIds - comma separated list of repository ids to query (or blank to query all)
* DG Finding Aid Inventory File (described above)

#### Output
* DSpace Item Ingest Report (repository id, resource id, resource title)
* DSpace Item Ingest folders

```
/.../as-ingest
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
/.../as-update
    /{resource-id}
        resource-ead.pdf
        dublin_core.xml - contains dc.identifier.uri for the source item (used to itentify the object to update)
```

#### DSpace Update
```
dspace itemupdate -e {eperson} -s /.../as-ingest/{dg-collection-handle} -A -i dc.identifier.uri
```

***
[![Georgetown University Library IT Code Repositories](https://raw.githubusercontent.com/Georgetown-University-Libraries/georgetown-university-libraries.github.io/master/LIT-logo-small.png)Georgetown University Library IT Code Repositories](http://georgetown-university-libraries.github.io/)

