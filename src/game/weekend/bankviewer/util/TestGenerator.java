package game.weekend.bankviewer.util;

import java.io.FileWriter;
import java.io.IOException;

public class TestGenerator {

	public static final String LS = System.lineSeparator();

	public static void main(String[] args) {
		System.out.println("Старт...");

		var tg = new TestGenerator();
		tg.makeTest("Выписка2.txt", 32, "26.07.2025");
		tg.makeTest("Выписка3.txt", 64, "27.07.2025");

		System.out.println("Готово!");
	}

	public void makeTest(String fileName, int numberOfLines, String docDate) {

		try (FileWriter writer = new FileWriter(fileName)) {

			// Записываю заголовк
			writer.write(ls("ClientBankExchange"));
			writer.write(ls("ВерсияФормата=1.00"));
			writer.write(ls("Кодировка=Windows"));
			writer.write(ls("ДатаСоздания=" + docDate));
			writer.write(ls("ВремяСоздания=10:11:12"));
			writer.write(ls("ДатаНачала=" + docDate));
			writer.write(ls("ДатаКонца=" + docDate));
			writer.write(ls("РасчСчет=00000000000000000000"));
			writer.write(ls("Что-то ещё?=Да, что-то ещё"));

			// Записываю документы
			for (int i = 1; i <= numberOfLines; ++i) {

				int counterparty = (int) (Math.random() * 20 + 10);

				writer.write(ls("СекцияДокумент=Платежное поручение"));

				writer.write(ls("Дата=" + docDate));
				writer.write(ls("Номер=" + ((int) (Math.random() * 1000 + 123))));
				writer.write(ls("Сумма=" + String.format("%.2f", Math.random() * 10000 + 123)));
				writer.write(ls("ПлательщикСчет=000000000000000000" + counterparty));
				writer.write(ls("ПолучательСчет=00000000000000000000"));
				writer.write(ls("ПлательщикИНН=00000000" + counterparty));
				writer.write(ls("Плательщик=Общество с ограниченной ответственностью Номер " + counterparty));
				writer.write(ls("ПлательщикБИК=0000000" + counterparty));
				writer.write(ls("ПлательщикБанк=Банк номер " + counterparty));
				writer.write(ls("ПолучательИНН=0000000000"));
				writer.write(ls("Получатель=Некоторое ООО"));

				if (counterparty % 5 == 0) {
					writer.write(ls("РедкоеПоле2=Редкое поле №2"));
					writer.write(ls("РедкаяДата2=" + docDate));
					writer.write(ls("РедкийНомер2=" + counterparty));
				}

				writer.write(ls("НазначениеПлатежа=Оплата за всяческие товары и услуги"));
				writer.write(ls("ПустоеПоле="));
				writer.write(ls("КакаяНибудьДата=" + docDate));
				writer.write(ls("КакойНибудьНомер=" + counterparty));

				if (counterparty % 5 == 0) {
					writer.write(ls("РедкоеПоле=Редкое поле"));
					writer.write(ls("Редкая датаДата=" + docDate));
					writer.write(ls("Редкий номер=" + counterparty));
				}
				writer.write(ls("КонецДокумента"));
			}

			// Записываю завершение файла
			writer.write(ls("КонецФайла"));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String ls(String s) {
		return s + LS;
	}
}
