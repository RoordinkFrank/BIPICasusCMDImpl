package nl.hu.schoolproject.BIPICasusCMDImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import filemanagment.filemanager.FileManagerException;
import nl.hu.schoolproject.BIPICasus.InvoiceExportFormat;
import nl.hu.schoolproject.BIPICasus.MongoConn;
import nl.hu.schoolproject.BIPICasus.model.Adress;
import nl.hu.schoolproject.BIPICasus.model.BTWCode;
import nl.hu.schoolproject.BIPICasus.model.Bedrijf;
import nl.hu.schoolproject.BIPICasus.model.Factuur;
import nl.hu.schoolproject.BIPICasus.model.Klant;
import nl.hu.schoolproject.BIPICasus.model.Product;

public class CommandLineProgram {
	public static void main(String[] args) {
		System.out.println("BIPICasusCMDImpl starting...");
		// TODO code application logic here
		insertInDB();
		List<Factuur> montlyFacturen = MongoConn.retrieveMontlyFacturen(1992, 9);
		System.out.println("Aantal montly Facturen retrieved: "+montlyFacturen.size());
		System.out.println("BIPICasus.retrieveMontlyFacturen retrieved: ");
		for (Factuur factuur : montlyFacturen){
			System.out.println(factuur);
		}
		cleanup();
		
		System.out.println("Creating Invoice...");
		InvoiceExportFormat invoiceFormat = new InvoiceExportFormat();
		Map<String, Bedrijf> bedrijven = new HashMap<String, Bedrijf>();
		Map<String, Adress> adressen = new HashMap<String, Adress>();
		adressen.put("normal", new Adress(122, "POST45", "utrecht", "ganzentraat"));
		bedrijven.put("werkendBedrijf", new Bedrijf("werkendBedrijf", adressen.get("normal"), "leBTWNummer" , "ING212423432", "32443FRT"));
		try {
			Bedrijf bedrijf = bedrijven.get("werkendBedrijf");
			System.out.println("Bedrijfserrors: "+bedrijf.checkConstructionErrors());
			System.out.println(bedrijf);
			for (Factuur factuur : montlyFacturen) {
				System.out.println("factuurserrors van "+factuur.getNummer()+": "+factuur.checkConstructionErrors());
			}
			System.out.println(montlyFacturen);
			invoiceFormat.createInvoice(bedrijf, montlyFacturen);
		} catch (FileManagerException e) {
			e.printStackTrace();
		}
	}

	public static void insertInDB() {
		
		List<Factuur> facturen = new ArrayList<Factuur>();
		List<Product> producten = new ArrayList<Product>();
		producten.add(new Product(0, "BifiTestProduct", 10, 12, BTWCode.LAAG, "Euro"));
		facturen.add(new Factuur(LocalDateTime.of(1992, 9, 28, 4, 8, 4), 3, new Klant("Frank"), producten.get(0)));
		facturen.add(new Factuur(LocalDateTime.of(1992, 9, 26, 4, 4, 4), 4, new Klant("Daan"), producten.get(0)));
		facturen.add(new Factuur(LocalDateTime.of(1991, 9, 26, 4, 4, 4), 5, new Klant("Joost"), producten.get(0)));
	    for (Factuur factuur : facturen) {
			MongoConn.insertFactuur(factuur);
	    }
	}
	
	private static void cleanup() {
		MongoConn.removeFactuur(3);
		MongoConn.removeFactuur(4);
		MongoConn.removeFactuur(5);
		//remove factuur werkt niet als er eerder wat (rood) crasht in de unit test.
		//vandaar dat het in een aparte cleanup methode staat.
	}
}