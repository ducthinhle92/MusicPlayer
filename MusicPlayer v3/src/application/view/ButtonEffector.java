package application.view;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class ButtonEffector extends ImageView {
	public ButtonEffector(Image normal, Image pressed) {
		super(normal);
		
		DropShadow shadow = new DropShadow();
		setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				setEffect(shadow);
			}
		});
		setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				setEffect(null);
			}
		});
	}
	
	public static void setGraphic(Button button, String normal, String hover) {
		ImageView ivNormal = new ImageView(normal);
		ImageView ivHover = new ImageView(hover);
		button.setGraphic(ivNormal);
		
		button.addEventHandler(MouseEvent.MOUSE_ENTERED, 
				new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				button.setGraphic(ivHover);
			}			
		});
		
		button.addEventHandler(MouseEvent.MOUSE_EXITED, 
				new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				button.setGraphic(ivNormal);
			}
		});
		
		button.addEventHandler(MouseEvent.MOUSE_PRESSED,
				new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				button.setGraphic(ivNormal);
			}
		});
		
		button.addEventHandler(MouseEvent.MOUSE_RELEASED,
				new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				button.setGraphic(ivHover);
			}
		});
	}
	
	public static void addEffect(Button button) {
		DropShadow shadow = new DropShadow(BlurType.THREE_PASS_BOX, 
				Color.web("#00ffff"), 10, 0.3, 0, 0);
		button.addEventHandler(MouseEvent.MOUSE_ENTERED,
				new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				button.setEffect(shadow);
			}
		});
		
		button.addEventHandler(MouseEvent.MOUSE_EXITED,
				new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				button.setEffect(null);
			}
		});
	}

	public static void setToggleGraphic(Button button, 
			String img1, String img2) {
		button.addEventHandler(MouseEvent.MOUSE_CLICKED,
				new EventHandler<MouseEvent>() {
			int selectedImg = 1;
			ImageView graphic1 = new ImageView(img1);
			ImageView graphic2 = new ImageView(img2);
			@Override
			public void handle(MouseEvent arg0) {
				if(selectedImg == 1) {
					button.setGraphic(graphic2);
					selectedImg = 2;
				}
				else {
					button.setGraphic(graphic1);
					selectedImg = 1;
				}
			}
		});
	}
}
