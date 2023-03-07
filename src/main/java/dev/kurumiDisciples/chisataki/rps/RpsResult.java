package dev.kurumiDisciples.chisataki.rps;

import java.awt.Color;

/**
 * Rps output from the challenger's perspective
 */
public enum RpsResult {
	WIN(Color.green),
	LOSS(Color.red),
	TIE(Color.yellow);
	
	private Color color;
	
	private RpsResult(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return this.color;
	}
}
