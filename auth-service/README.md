### Token Generation URL  

 - **Make a Post call to this URL**
	 - http://localhost:5000/uas/oauth/token

### Data Needs to Provide 
 - Authorization - *Basic Auth* 
	 - Username: service-hi (Client Info)
	 - Password: 123456 (Client Info)
 - Body - *x-www-form-urlencoded* (User Info from Database)
	 - grant_type: password
	 - password: 123456
	 - username: forezp

Sample Token generated: 

    {
        "access_token": "0f2229c3-63c5-454e-a2db-9766d44660a6",
        "token_type": "bearer",
        "refresh_token": "c91fe1b8-c3bf-42d0-aa09-b376e48d3b46",
        "expires_in": 43199,
        "scope": "server"
    }
