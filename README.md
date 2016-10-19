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
## Command Line Options

### Create Resource Ingest Options

#### Input
* List of Finding Aid references in DG

#### Output
* Ingest folders: ../as-ingest/{dg-collection}/{resource-id}
* ../as-ingest/{dg-collection}/{resource-id}/resource-ead.pdf
* ../as-ingest/{dg-collection}/{resource-id}/contents
* ../as-ingest/{dg-collection}/{resource-id}/dublin_core.xml


***
[![Georgetown University Library IT Code Repositories](https://raw.githubusercontent.com/Georgetown-University-Libraries/georgetown-university-libraries.github.io/master/LIT-logo-small.png)Georgetown University Library IT Code Repositories](http://georgetown-university-libraries.github.io/)

