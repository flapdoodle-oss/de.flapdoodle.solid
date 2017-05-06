package de.flapdoodle.solid.types;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;

public final class ByteArray {

	private final byte[] data;

	private ByteArray(byte[] data) {
		this.data = Preconditions.checkNotNull(data, "data is null");
	}

	public byte[] data() {
		return copy(data);
	}

	public int length() {
		return data.length;
	}

	public InputStream asInputStream() {
		return new ByteArrayInputStream(data);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ByteArray other = (ByteArray) obj;
		if (!Arrays.equals(data, other.data))
			return false;
		return true;
	}

	public static ByteArray fromString(String stringData, Charset charset) {
		return new ByteArray(stringData.getBytes(charset));
	}

	public static ByteArray fromStream(ByteArrayOutputStream outputStream) {
		return new ByteArray(outputStream.toByteArray());
	}

	public static ByteArray fromArray(byte[] source) {
		return new ByteArray(copy(source));
	}

	public static byte[] copy(byte[] source) {
		return Arrays.copyOf(Preconditions.checkNotNull(source, "source is null"), source.length);
	}

	public static ByteArray fromStream(InputStream inputStream) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ByteStreams.copy(inputStream, bytes);
		return new ByteArray(bytes.toByteArray());
	}

	public String asString(Charset charset) {
		return new String(data, charset);
	}

}