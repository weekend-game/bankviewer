## BankViewer

The program opens text files of a bank statement, presented as key-value pairs and displays them in a table.

Eclipse was used for development. The program implements the Single Document Interface and is written using Java and Swing. Java 11 is used, but for such a simple program the Java version does not matter.

### How to run the program

Download the repository to your computer. Everything you need for the program is located in the app folder. Navigate to the app folder and run the program by double-clicking the BankViewer.jar file or, if the program doesn't start, double-click the BankViewer.bat file. If the program doesn't start, download and install Java 11 or later and repeat the steps above.

### How to open a project in Eclipse

In Eclipse, select "Import..." from the "File" menu. In the window that opens, select "Existing projects into workspace." Navigate to the folder with the downloaded repository and click "Finish." The project will open in Eclipse. In the Package Explorer (on the left side of the screen), double-click the BankViewer.java file. The file will open for editing (in the center of the screen). Run the program by pressing Ctrl+F11 or using your preferred method for running programs in Eclipse.

The project includes a separate program called TestGenerator. It is located in the game.weekend.bankviewer.util package. The program allows you to specify the file name and number of bank statement lines, and it will generate a test file for you. The BankStatement1.txt file was generated using TestGenerator.

### How to use the program

The program uses a single document interface. This means that upon launch, you'll see the familiar menu, toolbar, empty space in the center of the window for displaying the document, and a status bar at the very bottom.

There are three ways to open a bank statement for viewing.

The first method is to select "File" - "Open..." from the menu or use the button on the toolbar. This will open a dialog box where you can select a bank statement file, for example, the BankStatement1.txt file available in the repository.

The second method is to use the Drag and Drop feature, specifically dragging the bank statement file to the center of the application window.

The third method is good for previously opened files. Paths to recently opened files are displayed in the "File" menu. Simply select the desired file, and it will be displayed.

To find text on a bank statement, press Ctrl+F, button with the image of binoculars on the toolbar, or select "Find..." from the Edit menu. In the dialog box that opens, enter your search string, select case sensitivity, specify the search direction, and click "Find Next." You can continue searching from the open search window using the toolbar buttons or keyboard shortcuts (Ctrl+G or Ctrl+Shift+G).

Some operations can be performed by right-clicking to open the context menu.

The application's appearance can be customized using the View menu.

First, you can choose your preferred design style. Second, you can hide the toolbar and status bar. You can view your bank statement using only keyboard shortcuts and menu items, but the toolbar and status bar take up space. Third, you can select the interface language: Russian or English.

All settings, even the search string, are saved between sessions.

### How the program is written

A more detailed description of the implementation is on the project page: [Bankviewer](https://weekend-game.github.io/bankviewer.htm#ProgDescr)

### Results

The program turned out to be surprisingly simple and useful.

### It would be nice...

It would be nice to be able to change the font size of the displayed bank statement.
