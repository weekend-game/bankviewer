## BankViewer

Программа открывает текстовые фалы содержащие пары «ключ-значение» и отображает их в виде таблицы. Для разработки использую [Eclipse](https://www.eclipse.org/). Программа реализует [SDI](https://ru.wikipedia.org/wiki/Однодокументный_интерфейс) и написана с использованием [Java](https://docs.oracle.com/javase/tutorial/index.html) и [Swing](https://docs.oracle.com/javase/tutorial/uiswing/index.html). Использую Java 11, но для такой простой программы версия Java неважна.

Для отображения информации в виде таблицы Swing предоставляет класс JTable. Но есть ещё один способ. Это JEditorPane которому методом setPage(URL u) передали URL HTML-файла в котором сформированы табличные данные (`<table> ... </table>`). Использую второй вариант.  Итак, главное в программе это преобразовать текстовый файл «ключ-значение» в HTML-файл содержащий HTML-таблицу (`<table> ... </table>`). Все остальное это обеспечение удобства использования программы.

#### 19.07.2025 Каркас приложения
В главном классе приложения **BankViewer** создаю JFrame основного окна приложения. В JFrame располагаю JEditorPane для будущего отображения таблицы. Делаю систему запоминания и восстановления положения окна на экране между запусками программы (класс **Proper**). Окно приложения имеет меню и инструментальную линейку (toolbar). Делаются они из одних и тех же объектов класса AbstractAction, которые в линейке меню выглядят как пункты меню, а в инструментальной линейке, как пиктограммы. Для создания меню и инструментальной линейки создаю класс **Act**. В нём же будут методы для создания каждого AbstractAction из которых собираются меню и линейка. Приложение открывает файлы  пользователя, и для этого Swing предлагает готовое решение - JFileChooser. Легкую настройку этого объекта и придание удобства для конкретного использования берёт на себя класс **Filer**. Класс BankViewer содержит в себе методы для отображения сообщений. Это нехорошо. Они там «лишние». Но поскольку программа крайне проста, то это не затруднит её чтения. Собственно преобразование файла «ключ-значение» в HTML-файл программа пока не содержит.

#### 20.07.2025 Первая версия готового приложения
Создал класс **Convertor**. Ну, как класс? Набор статических методов и пару примитивных вложенных классов. Это реализует основную идею программы, а именно, преобразовывает исходный файл в HTML-файл. Преобразование вызываю в open() класса Filer. **Программа готова!** И ей присвоена версия 01.00.

И ещё. Сделал самостоятельную программу TestGenerator. Она лежит в пакете game.weekend.bankviewer.util. В тексте программы можно указать имя файла, количество строк банковской выписки и она сгенерирует тестовый файл. Выписка1.txt сгенерирован именно посредством TestGenerator.

#### 26.07.2025 Поиск в таблице. Статусная строка

Мой "заказчик" Андрей сказал, что прога отлично работает, но не хватает поиска. Надо уметь искать в отображённой таблице подстроки так же, как в любом текстовом редакторе.

Для этого создан класс **Finder**. Естественно, понадобилось окно для указания подстроки поиска и каких-то особенностей поиска (вниз, вверх, учитывать ли регистр). Для этого создан класс **FinderFrame**. Чтобы расположить элементы интерфейса в этом окне, идеально подходит менеджер расположения GridBagLayout, но он уж очень "многословен", во всяком случае, мне он таким представился. Поэтому для сокращения количества строк кода я сделал некоторое упрощение GridBagLayout - класс **GBL**. В случае удачного поиска найденный фрагмент подсвечивается в отображённой таблице, а в случае неудачи выдаётся сообщение в статусной строке. Для этого создан класс **StatusBar**. Это JPanel с тремя текстовыми полями. Крайне правое отведено для сообщений, а остальные два решено использовать для отображения количества строк и колонок в таблице. В меню и инструментальную линейку добавлены новые пункты/кнопки. Причём пункты "Вырезать", "Вставить" и "Удалить" добавлены только потому, что они присутствуют в большинстве приложений. Конкретно в этом приложении они не работают.

Обнаружил, что при нажатии кнопки в инструментальной линейке фокус передаётся на нажатую кнопку. Всё правильно. Но если я выделил текст в таблице и нажал кнопку в инструментальной линейке, то выделение снимается. Неожиданно. К счастью, оказалось, это легко исправить вызовом для кнопок линейки setRequestFocusEnabled(false).

**Программу можно использовать!** И ей присвоена версия 01.10.
