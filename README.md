Expense Droid app:

Description: An Android app to manage financial transactions.

Current features;
* Add/Delete/Update transactions
* Apply Date/Amount filter which persist after reopening the app

Planned features to add:
* Export/Import data
* Category selection for transactions
* Multiple accounts
* Filter search for title

Personal notes:

TODO:
* Fix dollar sign for negative amounts. It should be -$1 not $-1
* Fix style of "dialog_filter_date.xml"
* Refactor settings keys like "menu_filter_date_checkbox"
* Add help menu
* Add unit tests

TODO (less important):
* Fix datebase upgrade
* Add a reset database menu item


Features to skip for now:
* Category selection
* Multiple accounts


Database basic fields:

id, title, amount, date, notes

Fields to add later:

category