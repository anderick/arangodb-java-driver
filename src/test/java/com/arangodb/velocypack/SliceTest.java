package com.arangodb.velocypack;

import java.math.BigInteger;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class SliceTest {

	@Test
	public void isNone() {
		final byte[] vpack = { 0x00 };
		final Slice slice = new Slice(vpack);
		Assert.assertTrue(slice.isNone());
	}

	@Test
	public void isNull() {
		final byte[] vpack = { 0x18 };
		final Slice slice = new Slice(vpack);
		Assert.assertTrue(slice.isNull());
	}

	@Test
	public void isIllegal() {
		final byte[] vpack = { 0x17 };
		final Slice slice = new Slice(vpack);
		Assert.assertTrue(slice.isIllegal());
	}

	@Test
	public void booleanTrue() {
		final byte[] vpack = { 0x1a };
		final Slice slice = new Slice(vpack);
		Assert.assertTrue(slice.isBoolean());
		Assert.assertTrue(slice.isTrue());
		Assert.assertFalse(slice.isFalse());
	}

	@Test
	public void booleanFalse() {
		final byte[] vpack = { 0x19 };
		final Slice slice = new Slice(vpack);
		Assert.assertTrue(slice.isBoolean());
		Assert.assertTrue(slice.isFalse());
		Assert.assertFalse(slice.isTrue());
	}

	@Test
	public void isArray() {
		checkArray(new byte[] { 0x01 });
		checkArray(new byte[] { 0x02 });
		checkArray(new byte[] { 0x03 });
		checkArray(new byte[] { 0x04 });
		checkArray(new byte[] { 0x05 });
		checkArray(new byte[] { 0x06 });
		checkArray(new byte[] { 0x07 });
		checkArray(new byte[] { 0x08 });
		checkArray(new byte[] { 0x09 });
		checkArray(new byte[] { 0x13 });
	}

	private void checkArray(final byte[] vpack) {
		final Slice slice = new Slice(vpack);
		Assert.assertTrue(slice.isArray());
	}

	@Test
	public void isObject() {
		checkObject(new byte[] { 0x0b });
		checkObject(new byte[] { 0x0c });
		checkObject(new byte[] { 0x0d });
		checkObject(new byte[] { 0x0e });
		checkObject(new byte[] { 0x0f });
		checkObject(new byte[] { 0x10 });
		checkObject(new byte[] { 0x11 });
		checkObject(new byte[] { 0x12 });
		checkObject(new byte[] { 0x14 });
	}

	private void checkObject(final byte[] vpack) {
		final Slice slice = new Slice(vpack);
		Assert.assertTrue(slice.isObject());
	}

	@Test
	public void isDouble() {
		final byte[] vpack = { 0x1b };
		final Slice slice = new Slice(vpack);
		Assert.assertTrue(slice.isDouble());
		Assert.assertTrue(slice.isNumber());
	}

	@Test
	public void isUTCDate() {
		final byte[] vpack = { 0x1c };
		final Slice slice = new Slice(vpack);
		Assert.assertTrue(slice.isUTCDate());
	}

	@Test
	public void isExternal() {
		final byte[] vpack = { 0x1d };
		final Slice slice = new Slice(vpack);
		Assert.assertTrue(slice.isExternal());
	}

	@Test
	public void isMinKey() {
		final byte[] vpack = { 0x1e };
		final Slice slice = new Slice(vpack);
		Assert.assertTrue(slice.isMinKey());
	}

	@Test
	public void isMaxKey() {
		final byte[] vpack = { 0x1f };
		final Slice slice = new Slice(vpack);
		Assert.assertTrue(slice.isMaxKey());
	}

	@Test
	public void isInt() {
		checkInt(new byte[] { 0x20 });
		checkInt(new byte[] { 0x21 });
		checkInt(new byte[] { 0x22 });
		checkInt(new byte[] { 0x23 });
		checkInt(new byte[] { 0x24 });
		checkInt(new byte[] { 0x25 });
		checkInt(new byte[] { 0x26 });
		checkInt(new byte[] { 0x27 });
	}

	private void checkInt(final byte[] vpack) {
		final Slice slice = new Slice(vpack);
		Assert.assertTrue(slice.isInt());
		Assert.assertTrue(slice.isInteger());
		Assert.assertTrue(slice.isNumber());
	}

	@Test
	public void isUInt() {
		checkUInt(new byte[] { 0x28 });
		checkUInt(new byte[] { 0x29 });
		checkUInt(new byte[] { 0x2a });
		checkUInt(new byte[] { 0x2b });
		checkUInt(new byte[] { 0x2c });
		checkUInt(new byte[] { 0x2d });
		checkUInt(new byte[] { 0x2e });
		checkUInt(new byte[] { 0x2f });
	}

	private void checkUInt(final byte[] vpack) {
		final Slice slice = new Slice(vpack);
		Assert.assertTrue(slice.isUInt());
		Assert.assertTrue(slice.isInteger());
		Assert.assertTrue(slice.isNumber());
	}

	@Test
	public void isSmallInt() {
		checkSmallInt(new byte[] { 0x30 });
		checkSmallInt(new byte[] { 0x31 });
		checkSmallInt(new byte[] { 0x32 });
		checkSmallInt(new byte[] { 0x33 });
		checkSmallInt(new byte[] { 0x34 });
		checkSmallInt(new byte[] { 0x35 });
		checkSmallInt(new byte[] { 0x36 });
		checkSmallInt(new byte[] { 0x37 });
		checkSmallInt(new byte[] { 0x38 });
		checkSmallInt(new byte[] { 0x39 });
		checkSmallInt(new byte[] { 0x3a });
		checkSmallInt(new byte[] { 0x3b });
		checkSmallInt(new byte[] { 0x3c });
		checkSmallInt(new byte[] { 0x3d });
		checkSmallInt(new byte[] { 0x3e });
		checkSmallInt(new byte[] { 0x3f });
	}

	private void checkSmallInt(final byte[] vpack) {
		final Slice slice = new Slice(vpack);
		Assert.assertTrue(slice.isSmallInt());
		Assert.assertTrue(slice.isInteger());
		Assert.assertTrue(slice.isNumber());
	}

	@Test
	public void isString() {
		checkString(new byte[] { 0x40 });
		checkString(new byte[] { 0x41 });
		checkString(new byte[] { 0x42 });
		checkString(new byte[] { 0x43 });
		checkString(new byte[] { 0x44 });
		checkString(new byte[] { 0x45 });
		checkString(new byte[] { 0x46 });
		checkString(new byte[] { 0x47 });
		checkString(new byte[] { 0x48 });
		checkString(new byte[] { 0x49 });
		checkString(new byte[] { 0x4a });
		checkString(new byte[] { 0x4b });
		checkString(new byte[] { 0x4c });
		checkString(new byte[] { 0x4d });
		checkString(new byte[] { 0x4e });
		checkString(new byte[] { 0x4f });
		checkString(new byte[] { 0x50 });
		checkString(new byte[] { 0x51 });
		checkString(new byte[] { 0x52 });
		checkString(new byte[] { 0x53 });
		checkString(new byte[] { 0x54 });
		checkString(new byte[] { 0x55 });
		checkString(new byte[] { 0x56 });
		checkString(new byte[] { 0x57 });
		checkString(new byte[] { 0x58 });
		checkString(new byte[] { 0x59 });
		checkString(new byte[] { 0x5a });
		checkString(new byte[] { 0x5b });
		checkString(new byte[] { 0x5c });
		checkString(new byte[] { 0x5d });
		checkString(new byte[] { 0x5e });
		checkString(new byte[] { 0x5f });
		checkString(new byte[] { 0x60 });
		checkString(new byte[] { 0x61 });
		checkString(new byte[] { 0x62 });
		checkString(new byte[] { 0x63 });
		checkString(new byte[] { 0x64 });
		checkString(new byte[] { 0x65 });
		checkString(new byte[] { 0x66 });
		checkString(new byte[] { 0x67 });
		checkString(new byte[] { 0x68 });
		checkString(new byte[] { 0x69 });
		checkString(new byte[] { 0x6a });
		checkString(new byte[] { 0x6b });
		checkString(new byte[] { 0x6c });
		checkString(new byte[] { 0x6d });
		checkString(new byte[] { 0x6e });
		checkString(new byte[] { 0x6f });
		checkString(new byte[] { 0x70 });
		checkString(new byte[] { 0x71 });
		checkString(new byte[] { 0x72 });
		checkString(new byte[] { 0x73 });
		checkString(new byte[] { 0x74 });
		checkString(new byte[] { 0x75 });
		checkString(new byte[] { 0x76 });
		checkString(new byte[] { 0x77 });
		checkString(new byte[] { 0x78 });
		checkString(new byte[] { 0x79 });
		checkString(new byte[] { 0x7a });
		checkString(new byte[] { 0x7b });
		checkString(new byte[] { 0x7c });
		checkString(new byte[] { 0x7d });
		checkString(new byte[] { 0x7e });
		checkString(new byte[] { 0x7f });
		checkString(new byte[] { (byte) 0x80 });
		checkString(new byte[] { (byte) 0x81 });
		checkString(new byte[] { (byte) 0x82 });
		checkString(new byte[] { (byte) 0x83 });
		checkString(new byte[] { (byte) 0x84 });
		checkString(new byte[] { (byte) 0x85 });
		checkString(new byte[] { (byte) 0x86 });
		checkString(new byte[] { (byte) 0x87 });
		checkString(new byte[] { (byte) 0x88 });
		checkString(new byte[] { (byte) 0x89 });
		checkString(new byte[] { (byte) 0x8a });
		checkString(new byte[] { (byte) 0x8b });
		checkString(new byte[] { (byte) 0x8c });
		checkString(new byte[] { (byte) 0x8d });
		checkString(new byte[] { (byte) 0x8e });
		checkString(new byte[] { (byte) 0x8f });
		checkString(new byte[] { (byte) 0x90 });
		checkString(new byte[] { (byte) 0x91 });
		checkString(new byte[] { (byte) 0x92 });
		checkString(new byte[] { (byte) 0x93 });
		checkString(new byte[] { (byte) 0x94 });
		checkString(new byte[] { (byte) 0x95 });
		checkString(new byte[] { (byte) 0x96 });
		checkString(new byte[] { (byte) 0x97 });
		checkString(new byte[] { (byte) 0x98 });
		checkString(new byte[] { (byte) 0x99 });
		checkString(new byte[] { (byte) 0x9a });
		checkString(new byte[] { (byte) 0x9b });
		checkString(new byte[] { (byte) 0x9c });
		checkString(new byte[] { (byte) 0x9d });
		checkString(new byte[] { (byte) 0x9e });
		checkString(new byte[] { (byte) 0x9f });
		checkString(new byte[] { (byte) 0xa0 });
		checkString(new byte[] { (byte) 0xa1 });
		checkString(new byte[] { (byte) 0xa2 });
		checkString(new byte[] { (byte) 0xa3 });
		checkString(new byte[] { (byte) 0xa4 });
		checkString(new byte[] { (byte) 0xa5 });
		checkString(new byte[] { (byte) 0xa6 });
		checkString(new byte[] { (byte) 0xa7 });
		checkString(new byte[] { (byte) 0xa8 });
		checkString(new byte[] { (byte) 0xa9 });
		checkString(new byte[] { (byte) 0xaa });
		checkString(new byte[] { (byte) 0xab });
		checkString(new byte[] { (byte) 0xac });
		checkString(new byte[] { (byte) 0xad });
		checkString(new byte[] { (byte) 0xae });
		checkString(new byte[] { (byte) 0xaf });
		checkString(new byte[] { (byte) 0xb0 });
		checkString(new byte[] { (byte) 0xb1 });
		checkString(new byte[] { (byte) 0xb2 });
		checkString(new byte[] { (byte) 0xb3 });
		checkString(new byte[] { (byte) 0xb4 });
		checkString(new byte[] { (byte) 0xb5 });
		checkString(new byte[] { (byte) 0xb6 });
		checkString(new byte[] { (byte) 0xb7 });
		checkString(new byte[] { (byte) 0xb8 });
		checkString(new byte[] { (byte) 0xb9 });
		checkString(new byte[] { (byte) 0xba });
		checkString(new byte[] { (byte) 0xbb });
		checkString(new byte[] { (byte) 0xbc });
		checkString(new byte[] { (byte) 0xbd });
		checkString(new byte[] { (byte) 0xbe });
		checkString(new byte[] { (byte) 0xbf });
	}

	private void checkString(final byte[] vpack) {
		final Slice slice = new Slice(vpack);
		Assert.assertTrue(slice.isString());
	}

	@Test
	public void isBinary() {
		checkBinary(new byte[] { (byte) 0xc0 });
		checkBinary(new byte[] { (byte) 0xc1 });
		checkBinary(new byte[] { (byte) 0xc2 });
		checkBinary(new byte[] { (byte) 0xc3 });
		checkBinary(new byte[] { (byte) 0xc4 });
		checkBinary(new byte[] { (byte) 0xc5 });
		checkBinary(new byte[] { (byte) 0xc6 });
		checkBinary(new byte[] { (byte) 0xc7 });
	}

	private void checkBinary(final byte[] vpack) {
		final Slice slice = new Slice(vpack);
		Assert.assertTrue(slice.isBinary());
	}

	@Test
	public void isBCD() {
		checkBCD(new byte[] { (byte) 0xc8 });
		checkBCD(new byte[] { (byte) 0xc9 });
		checkBCD(new byte[] { (byte) 0xca });
		checkBCD(new byte[] { (byte) 0xcb });
		checkBCD(new byte[] { (byte) 0xcc });
		checkBCD(new byte[] { (byte) 0xcd });
		checkBCD(new byte[] { (byte) 0xce });
		checkBCD(new byte[] { (byte) 0xcf });
		checkBCD(new byte[] { (byte) 0xd0 });
		checkBCD(new byte[] { (byte) 0xd1 });
		checkBCD(new byte[] { (byte) 0xd2 });
		checkBCD(new byte[] { (byte) 0xd3 });
		checkBCD(new byte[] { (byte) 0xd4 });
		checkBCD(new byte[] { (byte) 0xd5 });
		checkBCD(new byte[] { (byte) 0xd6 });
		checkBCD(new byte[] { (byte) 0xd7 });
	}

	private void checkBCD(final byte[] vpack) {
		final Slice slice = new Slice(vpack);
		Assert.assertTrue(slice.isBCD());
	}

	@Test
	public void isCustom() {
		checkCustom(new byte[] { (byte) 0xf0 });
		checkCustom(new byte[] { (byte) 0xf1 });
		checkCustom(new byte[] { (byte) 0xf2 });
		checkCustom(new byte[] { (byte) 0xf3 });
		checkCustom(new byte[] { (byte) 0xf4 });
		checkCustom(new byte[] { (byte) 0xf5 });
		checkCustom(new byte[] { (byte) 0xf6 });
		checkCustom(new byte[] { (byte) 0xf7 });
		checkCustom(new byte[] { (byte) 0xf8 });
		checkCustom(new byte[] { (byte) 0xf9 });
		checkCustom(new byte[] { (byte) 0xfa });
		checkCustom(new byte[] { (byte) 0xfb });
		checkCustom(new byte[] { (byte) 0xfc });
		checkCustom(new byte[] { (byte) 0xfd });
		checkCustom(new byte[] { (byte) 0xfe });
		checkCustom(new byte[] { (byte) 0xff });
	}

	private void checkCustom(final byte[] vpack) {
		final Slice slice = new Slice(vpack);
		Assert.assertTrue(slice.isCustom());
	}

	@Test
	public void getBooleanTrue() {
		final byte[] vpack = { 0x1a };
		final Slice slice = new Slice(vpack);
		Assert.assertTrue(slice.getBoolean());
	}

	@Test
	public void getBooleanFalse() {
		final byte[] vpack = { 0x19 };
		final Slice slice = new Slice(vpack);
		Assert.assertFalse(slice.getBoolean());
	}

	@Test
	public void getDouble() {
		{
			final byte[] vpack = { 0x1b, 64, 96, -74, 102, 102, 102, 102, 102 };
			final Slice slice = new Slice(vpack);
			Assert.assertEquals(133.7, slice.getDouble(), 0.);
		}
		{
			final byte[] vpack = { 0x1b, -64, 96, -74, 102, 102, 102, 102, 102 };
			final Slice slice = new Slice(vpack);
			Assert.assertEquals(-133.7, slice.getDouble(), 0.);
		}
	}

	@Test
	public void getSmallInt() {
		checkSmallInt(0, new byte[] { 0x30 });
		checkSmallInt(1, new byte[] { 0x31 });
		checkSmallInt(2, new byte[] { 0x32 });
		checkSmallInt(3, new byte[] { 0x33 });
		checkSmallInt(4, new byte[] { 0x34 });
		checkSmallInt(5, new byte[] { 0x35 });
		checkSmallInt(6, new byte[] { 0x36 });
		checkSmallInt(7, new byte[] { 0x37 });
		checkSmallInt(8, new byte[] { 0x38 });
		checkSmallInt(9, new byte[] { 0x39 });
		checkSmallInt(-6, new byte[] { 0x3a });
		checkSmallInt(-5, new byte[] { 0x3b });
		checkSmallInt(-4, new byte[] { 0x3c });
		checkSmallInt(-3, new byte[] { 0x3d });
		checkSmallInt(-2, new byte[] { 0x3e });
		checkSmallInt(-1, new byte[] { 0x3f });
	}

	private void checkSmallInt(final int expecteds, final byte[] vpack) {
		final Slice slice = new Slice(vpack);
		Assert.assertEquals(expecteds, slice.getSmallInt());
	}

	@Test
	public void getInt() {
		checkInt(Short.MAX_VALUE, new byte[] { 0x21, 127, -1 });
		checkInt(Integer.MAX_VALUE, new byte[] { 0x23, 127, -1, -1, -1 });
		checkInt(Long.MAX_VALUE, new byte[] { 0x27, 127, -1, -1, -1, -1, -1, -1, -1 });
	}

	private void checkInt(final long expextedValue, final byte[] vpack) {
		final Slice slice = new Slice(vpack);
		Assert.assertEquals(expextedValue, slice.getInt());
	}

	@Test
	public void getUInt() {
		checkUInt(new BigInteger(String.valueOf(Short.MAX_VALUE)), new byte[] { 0x29, 127, -1 });
		checkUInt(new BigInteger(String.valueOf(Integer.MAX_VALUE)), new byte[] { 0x2b, 127, -1, -1, -1 });
		final BigInteger longMax = new BigInteger(String.valueOf(Long.MAX_VALUE));
		checkUInt(longMax, new byte[] { 0x2f, 127, -1, -1, -1, -1, -1, -1, -1 });
		checkUInt(longMax.add(longMax), new byte[] { 0x2f, -1, -1, -1, -1, -1, -1, -1, -2 });
	}

	private void checkUInt(final BigInteger expecteds, final byte[] vpack) {
		final Slice slice = new Slice(vpack);
		Assert.assertEquals(expecteds, slice.getUInt());
	}

	@Test
	public void getUTCDate() {
		final byte[] vpack = { 0x1c, 0, 0, 0, -114, 5, 115, 83, 0 };
		final Slice slice = new Slice(vpack);
		Assert.assertEquals(new Date(609976800000l), slice.getUTCDate());
	}

	@Test
	public void getString() {
		checkString("Hallo Welt!", new byte[] { 0x4b, 72, 97, 108, 108, 111, 32, 87, 101, 108, 116, 33 });
		checkString("Hello World!", new byte[] { 0x4c, 72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100, 33 });
		checkString(
			"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit cursus nunc,",
			new byte[] { (byte) 0xbf, 0, 0, 0, 0, 0, 0, 5, 88, 76, 111, 114, 101, 109, 32, 105, 112, 115, 117, 109, 32,
					100, 111, 108, 111, 114, 32, 115, 105, 116, 32, 97, 109, 101, 116, 44, 32, 99, 111, 110, 115, 101,
					99, 116, 101, 116, 117, 101, 114, 32, 97, 100, 105, 112, 105, 115, 99, 105, 110, 103, 32, 101, 108,
					105, 116, 46, 32, 65, 101, 110, 101, 97, 110, 32, 99, 111, 109, 109, 111, 100, 111, 32, 108, 105,
					103, 117, 108, 97, 32, 101, 103, 101, 116, 32, 100, 111, 108, 111, 114, 46, 32, 65, 101, 110, 101,
					97, 110, 32, 109, 97, 115, 115, 97, 46, 32, 67, 117, 109, 32, 115, 111, 99, 105, 105, 115, 32, 110,
					97, 116, 111, 113, 117, 101, 32, 112, 101, 110, 97, 116, 105, 98, 117, 115, 32, 101, 116, 32, 109,
					97, 103, 110, 105, 115, 32, 100, 105, 115, 32, 112, 97, 114, 116, 117, 114, 105, 101, 110, 116, 32,
					109, 111, 110, 116, 101, 115, 44, 32, 110, 97, 115, 99, 101, 116, 117, 114, 32, 114, 105, 100, 105,
					99, 117, 108, 117, 115, 32, 109, 117, 115, 46, 32, 68, 111, 110, 101, 99, 32, 113, 117, 97, 109, 32,
					102, 101, 108, 105, 115, 44, 32, 117, 108, 116, 114, 105, 99, 105, 101, 115, 32, 110, 101, 99, 44,
					32, 112, 101, 108, 108, 101, 110, 116, 101, 115, 113, 117, 101, 32, 101, 117, 44, 32, 112, 114, 101,
					116, 105, 117, 109, 32, 113, 117, 105, 115, 44, 32, 115, 101, 109, 46, 32, 78, 117, 108, 108, 97,
					32, 99, 111, 110, 115, 101, 113, 117, 97, 116, 32, 109, 97, 115, 115, 97, 32, 113, 117, 105, 115,
					32, 101, 110, 105, 109, 46, 32, 68, 111, 110, 101, 99, 32, 112, 101, 100, 101, 32, 106, 117, 115,
					116, 111, 44, 32, 102, 114, 105, 110, 103, 105, 108, 108, 97, 32, 118, 101, 108, 44, 32, 97, 108,
					105, 113, 117, 101, 116, 32, 110, 101, 99, 44, 32, 118, 117, 108, 112, 117, 116, 97, 116, 101, 32,
					101, 103, 101, 116, 44, 32, 97, 114, 99, 117, 46, 32, 73, 110, 32, 101, 110, 105, 109, 32, 106, 117,
					115, 116, 111, 44, 32, 114, 104, 111, 110, 99, 117, 115, 32, 117, 116, 44, 32, 105, 109, 112, 101,
					114, 100, 105, 101, 116, 32, 97, 44, 32, 118, 101, 110, 101, 110, 97, 116, 105, 115, 32, 118, 105,
					116, 97, 101, 44, 32, 106, 117, 115, 116, 111, 46, 32, 78, 117, 108, 108, 97, 109, 32, 100, 105, 99,
					116, 117, 109, 32, 102, 101, 108, 105, 115, 32, 101, 117, 32, 112, 101, 100, 101, 32, 109, 111, 108,
					108, 105, 115, 32, 112, 114, 101, 116, 105, 117, 109, 46, 32, 73, 110, 116, 101, 103, 101, 114, 32,
					116, 105, 110, 99, 105, 100, 117, 110, 116, 46, 32, 67, 114, 97, 115, 32, 100, 97, 112, 105, 98,
					117, 115, 46, 32, 86, 105, 118, 97, 109, 117, 115, 32, 101, 108, 101, 109, 101, 110, 116, 117, 109,
					32, 115, 101, 109, 112, 101, 114, 32, 110, 105, 115, 105, 46, 32, 65, 101, 110, 101, 97, 110, 32,
					118, 117, 108, 112, 117, 116, 97, 116, 101, 32, 101, 108, 101, 105, 102, 101, 110, 100, 32, 116,
					101, 108, 108, 117, 115, 46, 32, 65, 101, 110, 101, 97, 110, 32, 108, 101, 111, 32, 108, 105, 103,
					117, 108, 97, 44, 32, 112, 111, 114, 116, 116, 105, 116, 111, 114, 32, 101, 117, 44, 32, 99, 111,
					110, 115, 101, 113, 117, 97, 116, 32, 118, 105, 116, 97, 101, 44, 32, 101, 108, 101, 105, 102, 101,
					110, 100, 32, 97, 99, 44, 32, 101, 110, 105, 109, 46, 32, 65, 108, 105, 113, 117, 97, 109, 32, 108,
					111, 114, 101, 109, 32, 97, 110, 116, 101, 44, 32, 100, 97, 112, 105, 98, 117, 115, 32, 105, 110,
					44, 32, 118, 105, 118, 101, 114, 114, 97, 32, 113, 117, 105, 115, 44, 32, 102, 101, 117, 103, 105,
					97, 116, 32, 97, 44, 32, 116, 101, 108, 108, 117, 115, 46, 32, 80, 104, 97, 115, 101, 108, 108, 117,
					115, 32, 118, 105, 118, 101, 114, 114, 97, 32, 110, 117, 108, 108, 97, 32, 117, 116, 32, 109, 101,
					116, 117, 115, 32, 118, 97, 114, 105, 117, 115, 32, 108, 97, 111, 114, 101, 101, 116, 46, 32, 81,
					117, 105, 115, 113, 117, 101, 32, 114, 117, 116, 114, 117, 109, 46, 32, 65, 101, 110, 101, 97, 110,
					32, 105, 109, 112, 101, 114, 100, 105, 101, 116, 46, 32, 69, 116, 105, 97, 109, 32, 117, 108, 116,
					114, 105, 99, 105, 101, 115, 32, 110, 105, 115, 105, 32, 118, 101, 108, 32, 97, 117, 103, 117, 101,
					46, 32, 67, 117, 114, 97, 98, 105, 116, 117, 114, 32, 117, 108, 108, 97, 109, 99, 111, 114, 112,
					101, 114, 32, 117, 108, 116, 114, 105, 99, 105, 101, 115, 32, 110, 105, 115, 105, 46, 32, 78, 97,
					109, 32, 101, 103, 101, 116, 32, 100, 117, 105, 46, 32, 69, 116, 105, 97, 109, 32, 114, 104, 111,
					110, 99, 117, 115, 46, 32, 77, 97, 101, 99, 101, 110, 97, 115, 32, 116, 101, 109, 112, 117, 115, 44,
					32, 116, 101, 108, 108, 117, 115, 32, 101, 103, 101, 116, 32, 99, 111, 110, 100, 105, 109, 101, 110,
					116, 117, 109, 32, 114, 104, 111, 110, 99, 117, 115, 44, 32, 115, 101, 109, 32, 113, 117, 97, 109,
					32, 115, 101, 109, 112, 101, 114, 32, 108, 105, 98, 101, 114, 111, 44, 32, 115, 105, 116, 32, 97,
					109, 101, 116, 32, 97, 100, 105, 112, 105, 115, 99, 105, 110, 103, 32, 115, 101, 109, 32, 110, 101,
					113, 117, 101, 32, 115, 101, 100, 32, 105, 112, 115, 117, 109, 46, 32, 78, 97, 109, 32, 113, 117,
					97, 109, 32, 110, 117, 110, 99, 44, 32, 98, 108, 97, 110, 100, 105, 116, 32, 118, 101, 108, 44, 32,
					108, 117, 99, 116, 117, 115, 32, 112, 117, 108, 118, 105, 110, 97, 114, 44, 32, 104, 101, 110, 100,
					114, 101, 114, 105, 116, 32, 105, 100, 44, 32, 108, 111, 114, 101, 109, 46, 32, 77, 97, 101, 99,
					101, 110, 97, 115, 32, 110, 101, 99, 32, 111, 100, 105, 111, 32, 101, 116, 32, 97, 110, 116, 101,
					32, 116, 105, 110, 99, 105, 100, 117, 110, 116, 32, 116, 101, 109, 112, 117, 115, 46, 32, 68, 111,
					110, 101, 99, 32, 118, 105, 116, 97, 101, 32, 115, 97, 112, 105, 101, 110, 32, 117, 116, 32, 108,
					105, 98, 101, 114, 111, 32, 118, 101, 110, 101, 110, 97, 116, 105, 115, 32, 102, 97, 117, 99, 105,
					98, 117, 115, 46, 32, 78, 117, 108, 108, 97, 109, 32, 113, 117, 105, 115, 32, 97, 110, 116, 101, 46,
					32, 69, 116, 105, 97, 109, 32, 115, 105, 116, 32, 97, 109, 101, 116, 32, 111, 114, 99, 105, 32, 101,
					103, 101, 116, 32, 101, 114, 111, 115, 32, 102, 97, 117, 99, 105, 98, 117, 115, 32, 116, 105, 110,
					99, 105, 100, 117, 110, 116, 46, 32, 68, 117, 105, 115, 32, 108, 101, 111, 46, 32, 83, 101, 100, 32,
					102, 114, 105, 110, 103, 105, 108, 108, 97, 32, 109, 97, 117, 114, 105, 115, 32, 115, 105, 116, 32,
					97, 109, 101, 116, 32, 110, 105, 98, 104, 46, 32, 68, 111, 110, 101, 99, 32, 115, 111, 100, 97, 108,
					101, 115, 32, 115, 97, 103, 105, 116, 116, 105, 115, 32, 109, 97, 103, 110, 97, 46, 32, 83, 101,
					100, 32, 99, 111, 110, 115, 101, 113, 117, 97, 116, 44, 32, 108, 101, 111, 32, 101, 103, 101, 116,
					32, 98, 105, 98, 101, 110, 100, 117, 109, 32, 115, 111, 100, 97, 108, 101, 115, 44, 32, 97, 117,
					103, 117, 101, 32, 118, 101, 108, 105, 116, 32, 99, 117, 114, 115, 117, 115, 32, 110, 117, 110, 99,
					44 });
	}

	private void checkString(final String expecteds, final byte[] vpack) {
		final Slice slice = new Slice(vpack);
		Assert.assertEquals(expecteds, slice.getString());
	}

}
