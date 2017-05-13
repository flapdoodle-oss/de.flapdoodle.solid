/**
 * Copyright (C) 2017
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.solid.parser.path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class PathTest {
	
	@Test
	public void sample() {
		Path path = Path.parse("/foo/bar/:property-nix/:other/nix");
		assertEquals(5,path.parts().size());
		assertEquals("/foo/bar/",((Path.Static) path.parts().get(0)).fixed());
		assertEquals("property",((Path.Property) path.parts().get(1)).property());
		assertEquals("-nix/",((Path.Static) path.parts().get(2)).fixed());
		assertEquals("other",((Path.Property) path.parts().get(3)).property());
		assertEquals("/nix",((Path.Static) path.parts().get(4)).fixed());
	}
	
	@Test
	public void withFormatter() {
		Path path = Path.parse("/foo/bar/:property#number-nix/:other/nix");
		assertEquals(5,path.parts().size());
		assertEquals("/foo/bar/",((Path.Static) path.parts().get(0)).fixed());
		assertEquals("property",((Path.Property) path.parts().get(1)).property());
		assertEquals("number",((Path.Property) path.parts().get(1)).formatter().get());
		assertEquals("-nix/",((Path.Static) path.parts().get(2)).fixed());
		assertEquals("other",((Path.Property) path.parts().get(3)).property());
		assertFalse(((Path.Property) path.parts().get(3)).formatter().isPresent());
		assertEquals("/nix",((Path.Static) path.parts().get(4)).fixed());
	}
}
