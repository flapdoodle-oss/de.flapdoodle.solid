package de.flapdoodle.solid.parser.path;

import static org.junit.Assert.assertEquals;

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
}
