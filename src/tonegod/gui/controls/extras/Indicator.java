/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.extras;

import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author t0neg0d
 */
public class Indicator extends Element {
	public static enum Orientation {
		HORIZONTAL,
		VERTICAL
	}
	private float maxValue = 0, currentValue = 0, percentage = 0;
	private Orientation orientation;
	private ColorRGBA indicatorColor;
	private String alphaMapPath;
	private String overlayImg;
	private Element elIndicator, elOverlay;
	private boolean displayValues = false, displayPercentages = false;
	
	/**
	 * Creates a new instance of the Indicator control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 */
	public Indicator(Screen screen, String UID, Vector2f position, Orientation orientation) {
		this(screen, UID, position,
			screen.getStyle("Window").getVector2f("defaultSize"),
			screen.getStyle("Window").getVector4f("resizeBorders"),
			screen.getStyle("Window").getString("defaultImg"),
			orientation
		);
	}
	
	/**
	 * Creates a new instance of the Indicator control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 */
	public Indicator(Screen screen, String UID, Vector2f position, Vector2f dimensions, Orientation orientation) {
		this(screen, UID, position, dimensions,
			screen.getStyle("Window").getVector4f("resizeBorders"),
			screen.getStyle("Window").getString("defaultImg"),
			orientation
		);
	}
	
	/**
	 * Creates a new instance of the Indicator control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param UID A unique String identifier for the Element
	 * @param position A Vector2f containing the x/y position of the Element
	 * @param dimensions A Vector2f containing the width/height dimensions of the Element
	 * @param resizeBorders A Vector4f containg the border information used when resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg The default image to use for the Slider's track
	 */
	public Indicator(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg, Orientation orientation) {
		super(screen, UID, position, dimensions, resizeBorders, null);
		
		this.overlayImg = defaultImg;
		this.orientation = orientation;
		
		elIndicator = new Element(
			screen,
			UID + ":Indicator",
			new Vector2f(0,0),
			dimensions.clone(),
			resizeBorders.clone(),
			null
		) {
			@Override
			public void updateLocalClipping() {
				Indicator ind = ((Indicator)this.getElementParent());
				if (getIsVisible()) {
					if (getClippingLayer() != null) {
						if (ind.getOrientation() == Indicator.Orientation.HORIZONTAL) {
							getClippingBounds().set(
								getClippingLayer().getAbsoluteX(),
								getClippingLayer().getAbsoluteY(),
								getClippingLayer().getAbsoluteWidth()-(getClippingLayer().getWidth()-ind.getCurrentPercentage()),
								getClippingLayer().getAbsoluteHeight()
							);
						} else {
							getClippingBounds().set(
								getClippingLayer().getAbsoluteX(),
								getClippingLayer().getAbsoluteY(),
								getClippingLayer().getAbsoluteWidth(),
								getClippingLayer().getAbsoluteHeight()-(getClippingLayer().getHeight()-ind.getCurrentPercentage())
							);
						}
						getElementMaterial().setVector4("Clipping", getClippingBounds());
						getElementMaterial().setBoolean("UseClipping", true);
					} else {
						getElementMaterial().setBoolean("UseClipping", false);
					}
				} else {
					getClippingBounds().set(0,0,0,0);
					getElementMaterial().setVector4("Clipping", getClippingBounds());
					getElementMaterial().setBoolean("UseClipping", true);
				}
				//setFontPages();
			}
		};
		elIndicator.setClippingLayer(elIndicator);
		elIndicator.setIgnoreMouse(true);
		addChild(elIndicator);
		
		elOverlay = new Element(
			screen,
			UID + ":Overlay",
			new Vector2f(0,0),
			dimensions.clone(),
			resizeBorders.clone(),
			overlayImg
		);
		elOverlay.setIgnoreMouse(true);
		elOverlay.setTextAlign(BitmapFont.Align.Center);
		elOverlay.setTextVAlign(BitmapFont.VAlign.Center);
		
		addChild(elOverlay);
		
	}
	
	public Orientation getOrientation() {
		return this.orientation;
	}
	
	public void setIndicatorColor(ColorRGBA indicatorColor) {
		this.indicatorColor = indicatorColor;
		elIndicator.getElementMaterial().setColor("Color", this.indicatorColor);
	}
	
	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
		refactorIndicator();
	}
	
	public float getMaxValue() {
		return this.maxValue;
	}
	
	public void setCurrentValue(float currentValue) {
		this.currentValue = currentValue;
		refactorIndicator();
	}
	
	public float getCurrentValue() {
		return this.currentValue;
	}
	
	private void refactorIndicator() {
		if (currentValue > maxValue) {
			currentValue = maxValue;
		} else if (currentValue < 0) {
			currentValue = 0;
		}
		percentage = currentValue/maxValue;
		if (alphaMapPath == null) {
			if (orientation == Orientation.HORIZONTAL) {
				percentage *= getWidth();
				elIndicator.setWidth(percentage);
			} else {
				percentage *= getHeight();
				elIndicator.setHeight(percentage);
			}
		} else {
			if (orientation == Orientation.HORIZONTAL) {
				percentage *= getWidth();
			} else {
				percentage *= getHeight();
			}
			elIndicator.updateLocalClipping();
		}
		
		if (this.displayValues) {
			elOverlay.setText(String.valueOf((int)this.currentValue) + "/" + String.valueOf((int)this.maxValue));
		} else if (this.displayPercentages) {
			elOverlay.setText(String.valueOf((int)this.percentage) + "%");
		} else {
			elOverlay.setText("");
		}
	}
	
	public float getCurrentPercentage() {
		return this.percentage;
	}
	
	public void setIndicatorAlphaMap(String alphaMapPath) {
		this.alphaMapPath = alphaMapPath;
		elIndicator.setAlphaMap(this.alphaMapPath);
	}
	
	public Element getTextDisplayElement() {
		return this.elOverlay;
	}
	
	public void setDisplayValues() {
		this.displayPercentages = false;
		this.displayValues = true;
	}
	
	public void setDisplayPercentage() {
		this.displayPercentages = true;
		this.displayValues = false;
	}
	
	public void setHideText() {
		this.displayPercentages = false;
		this.displayValues = false;
	}
}
