package jsonmerger;

import java.io.*;
import java.util.*;
import org.json.*;

public class JsonMerger {

	public static void main(String[] args) throws JSONException, IOException {

		File folder = new File("./Disqus file/");
		File[] listOfFiles = folder.listFiles();
		HashMap<String, ArrayList<String>> fileMap = new HashMap<>();

		// Add all file names into a map
		// The map has the format:
		// ArticleID : [ArrayList of file names in time order]
		for (int i = 0; i < listOfFiles.length; i++) {
			String fileName = listOfFiles[i].getName();
			if (!fileMap.containsKey(getArticleID(fileName))) {
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(fileName);
				fileMap.put(getArticleID(fileName), temp);
				System.out.println();
			} else if (fileMap.containsKey(getArticleID(fileName))) {
				ArrayList<String> temp = fileMap.get(getArticleID(fileName));
				for (int j = 0; j < temp.size(); j++) {
					if (compareArticleDate(fileName, temp.get(j)) < 0) {
						temp.add(j, fileName);
						break;
					} else if (j == temp.size() - 1) {
						temp.add(temp.size(), fileName);
						break;
					}
				}
				fileMap.put(getArticleID(fileName), temp);
			}
		}
		
		for(String articleID : fileMap.keySet()) {
			System.out.println("Article ID: " + articleID);
			System.out.println(fileMap.get(articleID).toString());
		}
		//Update all comment node in files
		//Write the updated version to a new file, named with article ID
		for (String articleID : fileMap.keySet()) {
			ArrayList<String> temp = fileMap.get(articleID);
			ArrayList<JSONObject> jsonArray = new ArrayList<>();
			
			//Iterate the array of file names with same articleID
			for (int i = 0; i < temp.size(); i++) {
				String fileName = "./Disqus file/"+ temp.get(i);
				//Put file content to jsonArray if not already did
				if (jsonArray == null) {
					parseJsonArray(jsonArray, fileName);
				//Already put, update the jsonArray
				} else {
					ArrayList<JSONObject> jsonUpdate = new ArrayList<>();
					parseJsonArray(jsonUpdate, fileName);
					for (JSONObject updateJsonObject : jsonUpdate) {
						boolean alreadyHad = false;
						for (JSONObject srcJsonObject : jsonArray) {
							if (hasSameID(srcJsonObject, updateJsonObject)) {
								updateJsonElement(srcJsonObject, updateJsonObject);
								alreadyHad = true;
							}
						}
						if(!alreadyHad) {
							jsonArray.add(updateJsonObject);
						}
					}
				}
			}
			
			//Write the final version of jsonArray to file, named with article ID
			String fileNameWithArticleID = "./Output File/" + articleID + ".txt";
			PrintWriter writer = new PrintWriter(fileNameWithArticleID, "UTF-8");
			for(JSONObject element : jsonArray) {
				writer.println(element.toString());
			}
			writer.close();

		}

	}

	// Check if 2 comment node has same ID
	public static boolean hasSameID(JSONObject left, JSONObject right) {
		if (left.getString("id").equals(right.getString("id"))) {
			return true;
		} else {
			return false;
		}
	}

	// Turn content of file into array of JSONObject
	public static void parseJsonArray(ArrayList<JSONObject> jarray, String fileName) {
		try {
			File file = new File(fileName);
			BufferedReader br;
			br = new BufferedReader(new FileReader(file));
			String st;
			while ((st = br.readLine()) != null) {
				JSONObject json = new JSONObject(st);
				JSONArray respond = json.getJSONArray("response");
				for (int i = 0; i < respond.length(); i++) {
					jarray.add(respond.getJSONObject(i));
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("Cant read file!");
		} catch (IOException e) {
			System.out.println("IO error!");
		}
	}

	// Update the value of JSONObject src with the value of JSONObject added
	public static void updateJsonElement(JSONObject src, JSONObject added) {
		for (String key : src.keySet()) {
			if (added.has(key)) {
				src.put(key, added.get(key));
			}
		}
	}

	// Check if 2 files has same ArticleID
	public static boolean sameArticleID(String file1, String file2) {
		String file1ID = file1.substring(0, file1.indexOf("_"));
		String file2ID = file2.substring(0, file1.indexOf("_"));
		if (file1ID.equals(file2ID)) {
			return true;
		} else {
			return false;
		}
	}

	// with 2 files with same Article ID, check which datetime is more recent
	public static int compareArticleDate(String file1, String file2) {
		Long file1date = Long.parseLong(file1.substring(file1.indexOf("_") + 1, file1.indexOf(".")));
		Long file2date = Long.parseLong(file2.substring(file2.indexOf("_") + 1, file2.indexOf(".")));
		if (file1date > file2date) {
			return 1;
		} else if (file1date < file2date) {
			return -1;
		} else {
			return 0;
		}
	}

	// Get Article ID of a filename
	public static String getArticleID(String fileName) {
		return fileName.substring(0, fileName.indexOf("_"));
	}
}
