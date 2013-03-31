/*
 * (c) Jeroen van den Enden <info@endroid.nl>
 *
 * This source file is subject to the MIT license that is bundled
 * with this source code in the file LICENSE.
 */

package nl.endroid.app.platform.entity;

import nl.endroid.framework.Animation;
import nl.endroid.framework.Entity;

public class Sky extends Entity
{
	@Override
	protected void configure()
	{		
		addAnimation(new Animation("sky"));
	}
}