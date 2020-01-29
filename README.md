Softwareentwicklung und Projektmanagment Projekt 2017

Proseminar 1 Team 4

iKiTa
======================

## Checking out a database file with entries for testing

Copy the "local.db.mv.db" file from "db-orig/" to the project's root directory.

## Logging in using the local database

- example usernames are displayed at the login screen
- passwords are "passwd"


## Description

Verwaltungssoftware für Kindertagesstätten


## Project members

- André Potocnik
- Daniel Walder
- Fabio Valentini (chief)
- Kerstin Klabischnig
- Lucas Markovic


## Responsibilities

# Glossar: Fabio (GLOSSARY.md)

# Segment 0: Kerstin, Daniel

    Usermanagement: Person, Child
    
    Teacher (Daniel):
        * Person und Child anlegen
        * alle Persons anzeigen
        * alle Children anzeigen
        * Person und Child löschen
        * Person und Child ändern
        * Guardian bestätigen
        
    Parent (Kerstin):
        * Guardian anlegen
        * Daten von Child ändern
        * Daten von Guardian ändern
        * Daten von Parent/this ändern
        * alle Children anzeigen
        * alle Guardians anzeigen
        
# Segment 1:

    Core Features: Calendar, Contact List, Times, Lunch
    
    Child Calendar (Lucas):
        * alternative Abholzeiten
        * alternative Abholperson
        * Abwesenheit
        * Essen
        
    Teacher Calendar / Dienstplan + Tagesplaner (André)
        * which teacher works when
        * how many children are present each day
        * how many meals have to be ordered
        
    KiTaCalendar:
        * opening times
        * holidays
        * Lunch 1 + Lunch 2 (Name, Preis)
        * Maximalbelegung
        
    Contact List (Daniel)

    Tasks(Daniel)
        



# Segment 2:

    Core Features: Tasks, Email, PDF exports, Audit-Log, Sorting+Filtering of Views
    
    Audit-Log (Fabio)
    
    Mail (André):
        * Newsletter
        * Notifications
        * Lunch Reminder
        
	Stammdaten(Daniel)

    PDF (Daniel)

# Segment 3:

    Additional Features: Terminal, Picture Gallery, Message Board, Private Messaging
    
    Terminal (Lucas)
        * Teile der GUI nochmal implementieren für kleines Display
        
    Message Board + Private Messages (Fabio)
    
    Picture Gallery (André)

# Segment 4:

    Testing and Debugging (everyone)

