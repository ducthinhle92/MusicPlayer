package application.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class LyricsGatherer {

	private final static String service1 = "http://www.songlyrics.com";
	private final static String service2 = "http://lyrics.wikia.com/api.php";

	public static List<String> getSongLyrics1(String title, String artist,
			ObservableList<String> lyric) throws IOException {

		try {
			Connection conn = Jsoup.connect(service1 + "/"
					+ artist.replace(" ", "-").toLowerCase() + "/"
					+ title.replace(" ", "-").toLowerCase() + "-lyrics/");
			Document doc = conn.get();
			Element p = doc.select("p.songLyricsV14").get(0);
			for (Node e : p.childNodes()) {
				if (e instanceof TextNode) {
					String line = ((TextNode) e).getWholeText();
					line = removeNewLine(line);
					lyric.add(line);
				}
			}
			return lyric;
		} catch (RuntimeException e) {
			System.out.println("RuntimeException: " + e.getMessage());
			throw e;
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
			throw e;
		}
	}

	private static String removeNewLine(String line) {
		line = line.trim();
		return line.replaceFirst("\n", "");
	}

	public static List<String> getSongLyrics2(String title, String artist,
			ObservableList<String> lyric) throws IOException {
		try {
			URL apiEndPoint = new URL(service2 + "?func=getSong&artist="
					+ escape(artist) + "&song=" + escape(title) + "&fmt=xml");

			URLConnection connection = apiEndPoint.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String inputLine, songUrl = null;
			while ((inputLine = in.readLine()) != null) {
				if (inputLine.contains("http://")) {
					songUrl = inputLine.substring(6, inputLine.length() - 6);
					break;
				}
			}
			in.close();

			URL lyricURL = new URL(songUrl);
			connection = lyricURL.openConnection();
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String lyricData = null;
			while ((inputLine = in.readLine()) != null) {
				if (inputLine.contains("<div class='lyricbox'")) {
					int scriptEnd = inputLine.indexOf("</script>");
					lyricData = inputLine.substring(scriptEnd + 9);
					break;
				}
			}
			in.close();

			parseLyrics(lyricData, lyric);
			return lyric;

		} catch (MalformedURLException e) {
			System.out.println("Url not correct - " + e.getMessage());
			throw e;
		} catch (IOException e) {
			System.out.println("Cant connect to server - " + e.getMessage());
			throw e;
		}
	}

	public static List<String> parseLyrics(String htmlData, ObservableList<String> lyric2) {
		int cursor = 0;
		List<String> lyric = new ArrayList<String>();
		String line = null;
		while (cursor < htmlData.length()) {
			char c = htmlData.charAt(cursor);
			if (c == '<') {
				String lookAhead = "";
				try {
					lookAhead = htmlData.substring(cursor, cursor + 6);
				} catch (StringIndexOutOfBoundsException ex) {
					lookAhead = htmlData.substring(cursor);
				}
				if (lookAhead.startsWith("<b>")) {
					cursor += 3;
					continue;
				} else if (lookAhead.startsWith("</b>")) {
					cursor += 4;
					continue;
				} else if (lookAhead.startsWith("<br />")) {
					cursor += 6;
					if (line != null) {
						lyric.add(line);
						line = "";
					}
					continue;
				}
			} else if (c == '&') {
				String sylable = "";
				while (c != ';' && cursor < htmlData.length()) {
					sylable += c;
					cursor++;
					c = htmlData.charAt(cursor);
				}
				char chr = toUTFChar(sylable);
				line = (line == null) ? ("" + chr) : line + chr;
			}
			cursor++;
		}

		if (line != null && !line.equals(""))
			lyric.add(line);
		return lyric;
	}

	private static char toUTFChar(String sylable) {
		if (sylable.startsWith("&#")) {
			int charCode = Integer.parseInt(sylable.substring(2));
			char result = (char) charCode;
			return result;
		}
		return 0;
	}

	private static String escape(String phrase) {
		return phrase.replaceAll(" ", "%20");
	}

	public static List<String> getSongLyrics(String title, String artist,
			ObservableList<String> lyric) throws Exception {
		title = validate(title);
		artist = validate(artist);
		List<String> result = null;

		try {
			System.out.println("Trying get lyric with " + service1);
			result = LyricsGatherer.getSongLyrics1(title, artist, lyric);
			if (result != null && result.size() != 0) {
				return result;
			}
		} catch (Exception ex) {
		}
		
		try {
			System.out.println("Trying get lyric with " + service2);
			result = LyricsGatherer.getSongLyrics2(title, artist, lyric);
			if (result != null && result.size() != 0) {
				return result;
			}
		} catch (Exception ex) {
		}		
		
		throw new Exception("Can not retrieve lyric");
	}

	private static String validate(String string) {
		return string.replace(".", "").replace("/", " ").replace("\\", " ")
				.toLowerCase();
	}
}
