Feature: Entry

  As an Employee,
  I want request an entry
  So I can enter if there is capacity

  Scenario: Employee submits entry request
    When the client 1 requests an entry on "2020-06-15"
    #Then the entry request is queue
