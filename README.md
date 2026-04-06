# HW3 CSE360 - Individual Homework Assignment
This is an assignment that uses the team project code from the CSE360 Team Project Repository CSE_360_TP01 (as implemented 4/5/26).

### Cryptographic Failures
With our current implementation, passwords are stored directly into the database without 
hashing. The Database component going forward will need to verify how passwords are stored 
and check if sensitive data is exposed during login or with our error messages. Testing will focus 
on checking if passwords are stored as plaintext or if they are hashed. 

### CWE-20: Improper Input Validation
Input validation is currently limited and inconsistent across the system. PostStore and 
ReplyStore are the most impacted components. During testing we will check for empty inputs, 
boundary lengths, and invalid formatting (too many characters, whitespace, null values). 

### CWE-306: Missing Authentication for Critical Function
Critical actions depend on weak or minimal user validation. This is heavily tied to CWE-20. Post 
and Reply creation, and thread interaction logic need to be further updated. Testing should 
attempt actions without a valid login, actions with invalid or missing user ID, and verify if the 
system is enforcing authentication consistently. 

### CWE-476: Null Pointer Dereference
Database lookups and input may result in null values with our current implementation of 
PostStore and ReplyStore. Testing should attempt accessing non-existent post and reply IDs, 
check for null or missing inputs and verify if methods handle these null inputs safely without 
crashing the system.


These tests confirm that the system safely rejects invalid inputs and handles null or missing data without crashing.

---

### Authentication Testing (CWE-306)
Authentication behavior was tested through the Database class to verify:
- valid login succeeds  
- incorrect password is rejected  
- nonexistent users are rejected  
- blank username or password is rejected  
- role-based login restrictions are enforced  

In this revision of the code, these tests highlight current limitations in authentication enforcement and provide a foundation for future improvements.

---

### Authorization Prototype for Private Interactions (TP3)
A prototype authorization layer was implemented to support planned TP3 functionality for private staff-to-student interactions.

New classes introduced:
- PrivateInteractionAuthorizationService.java  
- StaffPrivatePost.java  
- PrivateReply.java  

This prototype centralizes access-control rules for:
- creating private posts  
- viewing private posts  
- replying to private posts  
- editing and deleting private replies  

The goal of this prototype is to validate authorization logic before integrating database storage and user interface components.

---

### Authorization Testing
A dedicated test class was created:
- PrivateInteractionAuthorizationServiceTests.java  

These tests verify:
- only authorized users can view or reply to private posts  
- unrelated users are blocked from access  
- only reply authors can modify or delete their replies  
- invalid or null inputs are handled safely  

This ensures that the core security rules for private interactions are enforced correctly.

---

### Summary of Improvements
This assignment adds structured validation, testing, and early-stage authorization design to the existing system. While it does not fully implement new features, it reduces risk by verifying critical behaviors and preparing the codebase for future expansion.
