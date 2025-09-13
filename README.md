## BankViewer

The program opens text files of a bank statement, presented as key-value pairs and displays them in a table.

Eclipse was used for development. The program implements the Single Document Interface and is written using Java and Swing. Java 11 is used, but for such a simple program the Java version does not matter.

You can launch the application: from Eclipse, by double-clicking on BankViewer.jar or, if it does not start, by double-clicking on BankViewer.bat. If the latter does not start the application, download and install Java 11 or newer and try the methods described above again.

The project has a standalone program TestGenerator. It is located in the game.weekend.bankviewer.util package. In the program text, you can specify the file name, the number of lines of the bank statement, and it will generate a test file. The file Vypiska1.txt was generated using TestGenerator.

During the work, I had to figure out the following.

Creating a Single Document Interface, namely: creating a central part for displaying a document, menu, toolbar, status bar, context menu that appears when you right-click.

Implement control of the menu item, context menu item and toolbar button "Copy". Depending on the presence of text selected in the document, the menu item and button become active or unavailable, that is, it is allowed to copy if there is something to copy. Implement specifying a file for display by means of:
	1. a dialog box; 
	2. Drag & Drop; 
	3. a list of recently opened files.
The program allows you to use Look and Fills installed on your computer. A search for a string in the displayed document is implemented. To create a search window, a simplification of the GridBagLayout class was created - the GBL class. The program saves various settings between work sessions. Such as: screen size, search string, the need to show the toolbar, the need to show the status bar, the L&F used, the paths to the five most recently opened files. The program is internationalized (i18n) and localized (l10n) for the Russian language.

A more detailed description of the implementation is on the project page: [Bankviewer](https://weekend-game.github.io/bankviewer.htm)
