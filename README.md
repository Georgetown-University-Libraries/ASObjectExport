Extract published resources using the ArchivesSpace API.
Generate FindingAid item objects in DSpace.

## Property File Syntax
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
* repositoryId - repository to query
* resourceExcludes - comma separated list of resource ids to exclude (optional)
* modificationDate - search for resources modified after a specific date (optional)

#### Ouptut
* JSON Array of resource metadata - listing all published resources (modified after modification date) in the repository excluding those in the exclusion list

### Get Resource EAD

#### Input
* repositoryId - repository to query
* resourceId - resource to extract
* output file - file name to use for exported resource

#### Output
* PDF output file

## Specific Workflows Built on Generic Operations
These operations are specific to the way the Georgetown University Library synchronizes content between ArchivesSpace and DSpace.  These operations would likely require modification if adopted by other institutions.

### 


### Create Resource Ingest Options

#### Input
* List of Finding Aid references in DG

#### Output
* Ingest folders

```
/as-ingest
  /{dg-collection}
    /{resource-id}
      resource-ead.pdf
      contents
      dublin_core.xml
```

***
[![Georgetown University Library IT Code Repositories](https://raw.githubusercontent.com/Georgetown-University-Libraries/georgetown-university-libraries.github.io/master/LIT-logo-small.png)Georgetown University Library IT Code Repositories](http://georgetown-university-libraries.github.io/)

