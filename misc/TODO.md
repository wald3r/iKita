To-do List
==========

## Glossar

ParentCalendar ... Elternkalender: Wochen/Monatsansichtansicht: pro Kind; Anwesendheit; Essen; Bring/Abholzeit; Bezugsperson

TeacherCalendar ... Personalkalender: Wochen/Monatsansichtansicht: Anzahl Personal, Kinder und Essen; Ferien, Feiertage 

Dadplaner ... Tagesplaner: Tagesansicht: Liste anwesende Kinder; Essen; Bring/Abholzeit; Bezugspersonen; Geburtstag


## Determine project name

see Issue #1


## Determine timeline of project, project concept plus milestones

TBD


## use cases

Which are the different scenarios the system should handle? Which not?

## Concept

system concept


## GUI Design

sketch


## system parts

- calender (day-child-teacher)
- user administration
- contact list, child sheet
- day planer
- (*) different pick up time
- absence
- lunch
- (*) check-in/-out terminal
- task parents
- email
- (*) picture gallery
- (*) message board
- (*) direkt message system
- audit-log
- sort und filter

(*) ... special feature


## Database and Model

- Child  (id, name, picture, birthday, comment, abholZeit, bringZeit, registrationDate, deletionDate, allergies, emergencyContact: Person)

- Person (id, name, picture, birthday, comment, role {ADMIN, EDUCATOR, PARENT, BEZUGSPERSON}, mailNotification: bool, lunchNotification: bool)
  - ParentDetails (personID, telnumPrivate, telnumWork, emailAddress)

- ParentChildRelationship (personID, childID)
- BezugspersonChildRelationship (personID, childID)

- ChildCalendarDay (childID, date, anwesenheit, bool: essen, abholzeitAbweichend: MaybeNull, bringZeitAbweichend: MaybeNull, abholPersonAbweichend: MaybeNull)
- ParentCalendarDay (personID, date, aufgabe, urgent: bool, startDate, endDate, done: bool)
- EducatorCalendarDay (personID, date, anwesenheit)

- PictureDetails (personID, picture_URI)
- PersonToPersonMessage (sender: Person, recipient: Person, header: String, message: String)

- ...


Decide on database storage backend (H2 file, JSON file, sqlite file, MySQL server, etc.)!


## Extra Features

- Language Settings (German/English) ?

