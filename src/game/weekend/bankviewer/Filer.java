package game.weekend.bankviewer;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * Работа с файлами.
 */
public class Filer {

	/** Расширение файлов */
	public static final String EXTENSION = "txt";

	/** Название файлов */
	public static final String DESCRIPTION = "*.txt - Текстовый файл банковской выгрузки";

	/**
	 * Создать объект работы с файлами.
	 * 
	 * @param viewer основной объект приложения.
	 */
	public Filer(BankViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * Открыть указнный файл и отобразить его.
	 * 
	 * @param file полный путь к открываемому файлу
	 */
	public void open(File file) {
		if (file != null) {
			if (file.exists()) {
				this.file = file;

				// Отображаю имя открытого файла в заголовке приложения
				this.viewer.getFrame().setTitle(BankViewer.APP_NAME + " - " + file.getPath());

				try {
					// Исходный файл будет переработан и записан вот в такой html-файл
					String dst = "temp_bankviewer_" + no++;
					File tempFile = File.createTempFile(dst, ".html");

					Convertor.convert(file.getPath(), tempFile);

					// Отображаю html-файл
					this.viewer.showFile(tempFile);

					// Удаляю, теперь уже не нужный html-файл
					tempFile.deleteOnExit();

				} catch (IOException e) {
					this.viewer.err("Не удалось создать временный файл для отображения таблицы.\n" + e);
				}

			} else {
				// Выдаю сообщение об этом неприятном событии
				this.viewer.err("Файл " + file.getPath() + " не найден.");
			}
		}
	}

	/**
	 * Получить файл для открытия посредством диалога открытия файла.
	 * 
	 * @return файл указанный пользователем или null, если пользователь отказался от
	 *         открытия файла.
	 */
	public File showDialogue() {
		JFileChooser chooser = getChooser(this.file);
		int result = chooser.showOpenDialog(viewer.getFrame());

		File selectedFile = null;
		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = chooser.getSelectedFile();
		}
		return selectedFile;
	}

	/**
	 * Получить стандартное диалоговое окно для открытия файла программы настроенное
	 * в соответствии с нуждами программы.
	 * 
	 * @param currentFile текущий редактируемый файл.
	 * 
	 * @return настроенное диалоговое окно.
	 */
	private JFileChooser getChooser(File currentFile) {
		JFileChooser chooser = new JFileChooser();
		String curDir = (currentFile == null) ? "." : currentFile.getPath();
		chooser.setCurrentDirectory(new File(curDir));

		chooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File file) {
				if (file.getName().toLowerCase().endsWith("." + EXTENSION)) {
					return true;
				}
				if (file.isDirectory()) {
					return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				return DESCRIPTION;
			}
		});

		return chooser;
	}

	private File file;
	private BankViewer viewer;

	private static int no = 1;
}
