# Route configuration:

## /subscription
### GET
- RequestBody:
    - UserID {String, UUID Format}

- Returns:
    - 200:
        - Array of objects:
            - GroupID {String, UUID Format}
            - GroupName {String}
            - SubscriptionDate {String, Date Format}
    - 404:
        - User not found
    - 500:
        - Internal server error

### POST
- RequestBody
    - UserID {String, UUID Format}
    - GroupID {String, UUID Format}

- Returns
    - 202:
        - User added to notification group
    - 404:
        - Group or User not found
    - 500
        - Internal server error

### DELETE
- RequestBody
    - UserID {String, UUID Format}
    - GroupID {String, UUID Format}

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
    - UserID {String, UUID Format}

- Returns:
    - 200:
        - Array of objects:
            - ID {String, UUID}
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
    - GroupID {String, UUID Format}
    - Label {String}
    - ImpactLevel {String}

- Returns:
    - 202:
        - Message sent
    - 500:
        - Internal Server Error
    - 404:
        - Subscription Group not found