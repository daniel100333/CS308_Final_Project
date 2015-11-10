package DESIGN.datafiles;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class SaveMaster implements IFileManager{
	private GameSaver myGameSaver;
	private LevelSaver myLevelSaver;

	public SaveMaster () {
		this.myGameSaver = new GameSaver();
		this.myLevelSaver = new LevelSaver();
	}

	public static void main (String[] args) {
		GameSaver q = new GameSaver();
		Spaceship s = new Spaceship();
		System.out.println("saving");
		Level level = new Level();
		level.addActor(s);
		ArrayList<Level> levelList = new ArrayList<Level>();
		levelList.add(level);
		GameData game = new GameData(new StateData(), new LevelData(levelList));
		q.saveGame(game, "src/DESIGN/datafiles/game.xml");
		
//		q.save(s);
	}

	@Override
	public void saveGame(GameData game, String fileName) {
		// TODO Auto-generated method stub
//		myGameSaver.saveGame(game, fileName));
	}

	@Override
	public GameData loadGame(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveState(StateData state, String fileName) {
		// TODO Auto-generated method stub

	}

	@Override
	public StateData loadState(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void saveLevel(LevelData level, String fileName) {
		// TODO Auto-generated method stub

	}


	@Override
	public LevelData loadLevel(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}
}
