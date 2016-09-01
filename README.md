Extract published resources using the ArchivesSpace API.
Generate FindingAid item objects in DSpace.

### Property File Syntax
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
[![Georgetown University Library IT Code Repositories](https://raw.githubusercontent.com/Georgetown-University-Libraries/georgetown-university-libraries.github.io/master/LIT-logo-small.png)Georgetown University Library IT Code Repositories](http://georgetown-university-libraries.github.io/)

