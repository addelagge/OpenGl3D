package RenderEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import Models.RawModel;

public class OBJLoader {
	static int texcounter, normcounter;
	static Instant start;
	
	public static RawModel loadObjFile(String fileName, Loader loader){
		long startTime = 0;
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
		ArrayList<Vector2f> textures = new ArrayList<Vector2f>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		float[] vertexArray = null;
		float[] normalsArray = null;
		float[] texturesArray = null;
		int[] indexArray = null;
		VertexEvaluator evaluator = new VertexEvaluator();
		
		try {
			File imageFile = new File("res\\"+fileName+".obj");
			BufferedReader reader = new BufferedReader(new FileReader(imageFile));
			String line;
			String[] splitLine;
			while(true){
				line = reader.readLine();
				splitLine = line.split(" ");
				if(line.startsWith("v ")){					
					Vertex vertex = new Vertex(new Vector3f(Float.parseFloat(splitLine[1]), Float.parseFloat(splitLine[2]), Float.parseFloat(splitLine[3])));
					vertices.add(vertex);
					evaluator.evaluateVertex(vertex);
				} else if(line.startsWith("vt ")){
					Vector2f texture = new Vector2f(Float.parseFloat(splitLine[1]), Float.parseFloat(splitLine[2]));
					textures.add(texture);
				} else if(line.startsWith("vn ")){
					Vector3f normal = new Vector3f(Float.parseFloat(splitLine[1]), Float.parseFloat(splitLine[2]), Float.parseFloat(splitLine[3]));
					normals.add(normal);
				} else if(line.startsWith("f ")){
					normalsArray = new float[vertices.size() *3];
					texturesArray = new float[vertices.size() *2];
					break;
				}				
			}
//			start = Instant.now();
			while(line != null){
				if(!line.startsWith("f ")){
					line = reader.readLine();
					continue;
				}
				splitLine = line.split(" ");
				String[] vertex1 = splitLine[1].split("/");
				String[] vertex2 = splitLine[2].split("/");
				String[] vertex3 = splitLine[3].split("/");
				
				processVertex(vertex1, vertices, indices);
				processVertex(vertex2, vertices, indices);
				processVertex(vertex3, vertices, indices);
				line = reader.readLine();
			}			
			reader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		indexArray = storeInIntArray(indices);
		normalsArray = new float[vertices.size() *3];
		texturesArray = new float[vertices.size() *2];
		vertexArray = new float[vertices.size() *3];
		storeVertexdataInArrays(vertices, normals, textures, vertexArray, texturesArray, normalsArray);
		
		RawModel blenderModel = loader.loadToVao(vertexArray, texturesArray, normalsArray, indexArray);
		blenderModel.setDimensions(evaluator.getDimensions());
		return blenderModel;
	}
	
	private static void storeVertexdataInArrays(ArrayList<Vertex> vertices, ArrayList<Vector3f> normals,
			ArrayList<Vector2f> textures, float[] vertexArray, float[] texturesArray, float[] normalsArray) {
		
		for(int i = 0; i < vertices.size(); i++){
			Vertex vertex = vertices.get(i);
			Vector3f position = vertex.getPosition();
			vertexArray[i * 3] = position.x;
			vertexArray[i * 3 + 1] = position.y;
			vertexArray[i * 3 + 2] = position.z;
			
			Vector2f texture = textures.get(vertex.getTextureIndex());
			texturesArray[i * 2] = texture.x;
			texturesArray[i * 2 + 1] = 1- texture.y;
			
			Vector3f normal = normals.get(vertex.getNormalIndex());
			normalsArray[i * 3] = normal.x;
			normalsArray[i * 3 + 1] = normal.y;
			normalsArray[i * 3 + 2] = normal.z;			
		}
		
	}

	private static int[] storeInIntArray(ArrayList<Integer> indices) {
		int[] indexArray = new int[indices.size()];
		for(int i = 0; i < indices.size(); i++){
			indexArray[i] = indices.get(i);
		}
		return indexArray;
	}

	private static void processVertex(String[] vertexData, ArrayList<Vertex> vertices, ArrayList<Integer> indices){
		int currentIndex = Integer.parseInt(vertexData[0]) -1;
		indices.add(currentIndex);
		Vertex currentVertex = vertices.get(currentIndex);
		if(!currentVertex.isVertexSet()){
			int textureIndex = Integer.parseInt(vertexData[1]) -1;
			int normalIndex = Integer.parseInt(vertexData[2]) -1;
			currentVertex.setTextureIndex(textureIndex);
			currentVertex.setNormalIndex(normalIndex);
		}	
	}

}
