package view.element;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import authoring.controller.AuthoringController;
import authoring.controller.constructor.configreader.AuthoringConfigManager;
import authoring.files.properties.ActorProperties;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import view.actor.ActorBrowserCell;
import view.level.Workspace;
import view.screen.AbstractScreenInterface;

/**
 * @author David
 * 
 *         A double list view element class that allows the user to see the
 *         level's current actor types. Also allows for drag and drop placement
 *         of new actor instances
 * 
 */
public class ActorBrowser extends AbstractDockElement {

	private static final String ACTOR_CONFIGURATION_DIRECTORY = "src/authoring/files/actors/%s.properties";
	private static final String CONFIGURATION_DIRECTORY = "src/authoring/files/%s.properties";
	private static final String CONFIGURATION = "configuration";
	private static final String DEFAULT_IMAGE = "rcd.jpg";
	
	
	private ListView<String> rightlist;
	private ListView<String> leftlist;
	private ArrayList<ListView<String>> lists;
	private ObservableList<String> actors;
	private BooleanProperty doubleLists;
	private AuthoringController controller;
	private GridPane listPane;

	public ActorBrowser(GridPane home, String title, AbstractScreenInterface screen, Workspace workspace) {
		super(home, title, screen);
		findResources();
		doubleLists = new SimpleBooleanProperty(true);
		doubleLists.addListener(e -> toggleDoubleLists(doubleLists.getValue()));
		this.controller = null;
		workspace.addListener((ov, oldTab, newTab) -> {
			if (workspace.getCurrentLevel() != null) {
				load(workspace.getCurrentLevel().getController());
			} else {
				load(null);
			}
		});
		makePane();
	}

	@Override
	protected void makePane() {
		pane.add(titlePane, 0, 0);
		listPane = new GridPane();
		pane.add(listPane, 0, 1);
		actors = FXCollections.observableArrayList(new ArrayList<String>());
		rightlist = new ListView<String>(actors);
		leftlist = new ListView<String>(actors);
		listPane.add(leftlist, 0, 1);
		listPane.add(rightlist, 1, 1);
		configure(leftlist);
		configure(rightlist);
		lists = new ArrayList<ListView<String>>();
		lists.add(leftlist);
		lists.add(rightlist);
		load(controller);
	}

	public void load(AuthoringController controller) {
		this.controller = controller;
		actors = FXCollections.observableArrayList(new ArrayList<String>());
		leftlist.setItems(actors);
		rightlist.setItems(actors);
		if (controller != null) {
			actors.addAll(controller.getAuthoringActorConstructor().getActorList());
			System.out.println(actors.toString());
		}
		// load new groups into ActorGroups
		actors.forEach(a -> {
			controller.getLevelConstructor().getActorGroupsConstructor().getActorGroups().addGroup(a);
		});
	}

	private void configure(ListView<String> list) {
		list.prefHeightProperty().bind(screen.getScene().heightProperty());
		list.setMaxWidth(Double.parseDouble(myResources.getString("width")));
		list.setFocusTraversable(false);
		list.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> list) {
				ActorBrowserCell cell = new ActorBrowserCell(controller);
				cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> handlePress(cell, list, event));
				cell.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> handleRelease(cell, list, event));
				cell.setOnDragDetected(e -> cell.drag(e));
				cell.setOnDragDone(e -> cell.dragDone(e));
				return cell;
			}
		});
	}

	private void handlePress(ActorBrowserCell cell, ListView<String> list, MouseEvent event) {
		if (!cell.isEmpty()) {
			int index = cell.getIndex();
			if (list.getSelectionModel().getSelectedIndices().contains(index)) {
				cell.markForDeselection();
			} else {
				list.getSelectionModel().select(index);
			}
			event.consume();
		}
	}

	private void handleRelease(ActorBrowserCell cell, ListView<String> list, MouseEvent event) {
		if (cell.deselect()) {
			int index = cell.getIndex();
			list.getSelectionModel().clearSelection(index);
		}
		event.consume();
	}

	/**
	 * @param actorGroupID Group ID of the new actor group that you want to create
	 * @param properties Properties of the new actor group
	 */
	public void addNewActor(String actorGroupID, Map<String, String> properties) {
		Properties prop = new Properties();
		OutputStream output = null;
		try {
			output = new FileOutputStream(String.format(ACTOR_CONFIGURATION_DIRECTORY, actorGroupID));
			String props = "";
			for (Map.Entry<String, String> property : properties.entrySet()) {
				props += property.getKey() + ",";
				prop.setProperty(property.getKey(), property.getValue().substring(0, property.getValue().length()));
			}
			prop.setProperty("properties", props);
			prop.store(output, "New Actor Group: "+actorGroupID);
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		FileInputStream in;
		Properties props = new Properties();
		try {
			in = new FileInputStream(String.format(CONFIGURATION_DIRECTORY, CONFIGURATION));
			props.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			FileOutputStream out = new FileOutputStream(String.format(CONFIGURATION_DIRECTORY, CONFIGURATION));
			String actors = props.getProperty("actors") + "," + actorGroupID;
			props.setProperty("actors", actors);
			props.store(out, null);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		AuthoringConfigManager.getInstance().refresh();
		load(controller);
	}


	public void createCustomGroup() {
		Map<String, String> properties = new HashMap<String, String>();
	    for(ActorProperties property: ActorProperties.values()){
	    	properties.put(property.getKey(), "0");
	    }
		TextInputDialog dialog = new TextInputDialog("walter");
		dialog.setTitle("Group Name Input");
		dialog.setHeaderText("Your new actor!");
		dialog.setContentText("Please enter your actor's name:");
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(name -> {
			properties.put("groupID", name);
//			TODO Add image_name from image selector
			properties.put("image", DEFAULT_IMAGE);
			addNewActor(name, properties);
		});
	}

	public BooleanProperty getDoubleListsProperty() {
		return doubleLists;
	}

	private void toggleDoubleLists(Boolean value) {
		if (value) {
			listPane.add(rightlist, 1, 1);
			setWidth(leftlist, Double.parseDouble(myResources.getString("width")));
			GridPane.setColumnSpan(titlePane, 2);
			listPane.setAlignment(Pos.TOP_CENTER);
		} else {
			rightlist.getSelectionModel().clearSelection();
			listPane.getChildren().remove(rightlist);
			setWidth(leftlist, 2 * Double.parseDouble(myResources.getString("width")));
			GridPane.setColumnSpan(titlePane, 1);
		}
	}

	public void setWidth(ListView list, double width) {
		list.setMinWidth(width);
		list.setMaxWidth(width);
	}

	public ArrayList<ListView<String>> getLists() {
		return lists;
	}

}
