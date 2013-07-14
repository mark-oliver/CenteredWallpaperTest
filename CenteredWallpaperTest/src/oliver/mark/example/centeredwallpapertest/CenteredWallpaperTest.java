/*
 * Copyright (C) 2013 Mark Oliver.
 * 
 *Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package oliver.mark.example.centeredwallpapertest;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;


public class CenteredWallpaperTest extends AnimationWallpaper {
	
	
	public class CenteredTestEngine extends AnimationEngine implements SharedPreferences.OnSharedPreferenceChangeListener {
		int pixelOffsetX = 0;
		int pixelOffsetY = 0;
		float offsetX = 0f;
		float offsetY = 0f;
		Paint paint = new Paint();
		int visibleWidth;
		int width, height;
		boolean drawCentered;
		
		public CenteredTestEngine() {
			super();
			SharedPreferences prefs = CenteredWallpaperTest.this.getSharedPreferences("settings", 0);
			prefs.registerOnSharedPreferenceChangeListener(this);
			this.onSharedPreferenceChanged(prefs, null);
			
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
				float xOffsetStep, float yOffsetStep, int xPixelOffset,
				int yPixelOffset) {
			pixelOffsetX = xPixelOffset;
			pixelOffsetY = yPixelOffset;
			
			offsetX = (-xOffset * this.visibleWidth);
			offsetY = 0;
			
			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
					xPixelOffset, yPixelOffset);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			this.height = height;
			this.visibleWidth = width;
			
			if (this.isPreview()) {
				this.width = width;
			} else {
				this.width = 2 * width;
			}
			super.onSurfaceChanged(holder, format, width, height);
		}



		@Override
		protected void drawFrame() {
			SurfaceHolder holder = getSurfaceHolder();
			
			Canvas c = null;
			try {
				c = holder.lockCanvas();
				if (c != null) {
					//which method are we using to draw the circle
					if (this.drawCentered) {
						drawOffset(c);
					} else {
						drawPixelOffset(c);
					}
				}
			} finally {
				if (c != null) {
					try {
						holder.unlockCanvasAndPost(c);
					} catch (IllegalArgumentException e) {
						//sometimes an exception gets thrown when
						//switching screen orientation
						//when this happens, do nothing
					}
				}
			}
			
		}

		private void drawOffset(Canvas c) {
			c.save();
			
			c.drawColor(Color.GRAY);
			
			paint.setAntiAlias(true);
			paint.setColor(Color.RED);
			c.drawCircle(this.width / 2 + this.offsetX, this.height / 2 + this.offsetY, this.visibleWidth / 10, paint);
		}

		private void drawPixelOffset(Canvas c) {
			c.save();
			
			c.drawColor(Color.GRAY);
			
			paint.setAntiAlias(true);
			paint.setColor(Color.RED);
			c.drawCircle(this.width / 2 + this.pixelOffsetX, this.height / 2 + this.pixelOffsetY, this.visibleWidth / 10, paint);
		}

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences prefs, String key) {
			this.drawCentered = prefs.getBoolean("draw_centered", false);
		}

	}

	@Override
	public Engine onCreateEngine() {
		// TODO Auto-generated method stub
		return new CenteredTestEngine();
	}

}
