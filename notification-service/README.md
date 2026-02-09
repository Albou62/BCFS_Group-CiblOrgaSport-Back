# Route configuration:

## /groups
### GET
- Returns
    - 200:
        - Array of objects:
            - GroupID
            - GroupName

### POST
- RequestBody
    - name {String}

- Returns
    - 200:
        - Group created in Database
    - 500:
        - Internal Server Error

## /subscription
### GET
- RequestBody:
    - UserID {String, UUID Format}

- Returns:
    - 200:
        - Array of objects:
            - GroupID {Long}
            - GroupName {String}
            - SubscriptionDate {String, Date Format}
    - 404:
        - User not found
    - 500:
        - Internal server error

### POST
- RequestBody
    - UserID {Long}
    - GroupID {Long}

- Returns
    - 202:
        - User added to notification group
    - 404:
        - Group or User not found
    - 500
        - Internal server error

### DELETE
- RequestBody
    - UserID {Long}
    - GroupID {Long}

- Returns
    - 200:
        - User removed from notification group
    - 500
        - Internal Server Error
    - 404
        - Group or User not found

## /notification
### GET
- RequestBody:
    - UserID {Long}

- Returns:
    - 200:
        - Array of objects:
            - ID {Long}
            - Date {String, Date Format}
            - Group {String}
            - Label {String}
            - ImpactLevel {String}
    - 500:
        - Internal Server Error
    - 404:
        - User not found

### POST
- RequestBody:
    - GroupID {Long}
    - Label {String}
    - ImpactLevel {String}

- Returns:
    - 202:
        - Message sent
    - 500:
        - Internal Server Error
    - 404:
        - Subscription Group not found