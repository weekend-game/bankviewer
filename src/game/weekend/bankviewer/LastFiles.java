package game.weekend.bankviewer;

import java.util.LinkedList;
import java.util.List;

/**
 * Список имен последних открытых файлов.
 * <p>
 * Имена файлов храняться между сеансами работы приложения посредством объекта
 * хранимых свойств приложения (Proper).
 * <p>
 * Использование. Объект создаётся при старте приложения. При этом он, используя
 * Proper, читает ранее сохранённый список файлов. Объект класса Act использует
 * метод getList(), для формирования списка последних открытых файлов в меню
 * File. Filer в случае успешного открытия очередного файла, методом put()
 * помещает имя открытого файла в список. Если открыть файл не удалось, то Filer
 * методом remove() удаляет такой файл из списка. При завершении приложения
 * класс BankViewer из своего метода close() вызывает save() и т.о. сохраняет
 * список файлов до следующего запуска приложения.
 */
public class LastFiles {

	/**
	 * Создать объект хранения имен последних открытых файлов.
	 * 
	 * @param maxSize максимальное количество хранимых имен файлов.
	 */
	public LastFiles(int maxSize) {
		// Максимальное количество хранимых имен файлов
		this.maxSize = maxSize;

		// Указанное количество будет прочитано в список list из хранимых
		// свойств приложения. Это свойства с именами File1, File2, ...
		for (int i = 1; i <= maxSize; ++i) {
			String s = Proper.getProperty("File" + i, "");
			if (s.length() > 0) {
				list.add(s);
			}
		}
	}

	/**
	 * Запомнить имя файла в списке.
	 * 
	 * @param value путь и имя открытого файла.
	 */
	public void put(String value) {
		// Возможно файл уже есть в списке, поэтому его надо удалить
		int i = list.indexOf(value);
		if (i >= 0) {
			list.remove(i);
		}
		list.addFirst(value); // И разместить первым в списке

		// Если размер списка вышел за пределы maxSize, то удаляю последний (забываю)
		if (list.size() > maxSize) {
			list.remove(maxSize);
		}
	}

	/**
	 * Удалить имя файла из списка.
	 */
	public void remove(String value) {
		int pos = list.indexOf(value);
		list.remove(pos);
	}

	/**
	 * Получить список последних открытых файлов. Метод используется для фрмирования
	 * списка этих файлов в меню File приложения.
	 * 
	 * @return список последних открытых файлов.
	 */
	public List<String> getList() {
		return list;
	}

	/**
	 * Сохранить список файлов в запоминаемых свойствах приложения. Это будут
	 * свойства с именами File1, File2, ... Они будут вновь прочитаны при создании
	 * этого объекта (при запуске приложения).
	 */
	public void save() {
		int i = 1;
		for (String s : list) {
			Proper.setProperty("File" + i, s);
			++i;
		}
		while (i <= maxSize) {
			Proper.setProperty("File" + i, "");
			++i;
		}
	}

	private final int maxSize;
	private final LinkedList<String> list = new LinkedList<String>();
}
