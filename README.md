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

## Specific Workflows Built on Generic Operations
These operations are specific to the way the Georgetown University Library synchronizes content between ArchivesSpace and DSpace.  These operations would likely require modification if adopted by other institutions.

### DG Finding Aid Inventory File
A CSV file listing the finding aids in DSpace.

* Collection Handle
* DSpace Item Id
* DSpace Item Title
* Finding Aid URL
* Bitstream last modification date (optional)

### Create Resource Ingest 

#### Input
* DG Finding Aid Inventory File (described above)

#### Output
* ASObject Export Property File (described above)
* DSpace Item Ingest Report (repository id, resource id, resource title)
* DSpace Item Ingest folders

```
/as-ingest
  /{dg-collection}
    /{resource-id}
      resource-ead.pdf
      contents
      dublin_core.xml
```

### Create Resource Update

#### Input
* ASObject Export Property File (described above)
* DG Finding Aid Inventory File (described above)
* modification date - find items modified since a particular date

***
[![Georgetown University Library IT Code Repositories](https://raw.githubusercontent.com/Georgetown-University-Libraries/georgetown-university-libraries.github.io/master/LIT-logo-small.png)Georgetown University Library IT Code Repositories](http://georgetown-university-libraries.github.io/)

