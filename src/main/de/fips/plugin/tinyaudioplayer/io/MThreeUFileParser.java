/*
Copyright � 2011 Philipp Eichhorn.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package de.fips.plugin.tinyaudioplayer.io;

import java.io.File;
import java.io.IOException;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MThreeUFileParser {
	private final IPlaylistFileVisitor visitor;
	
	public void parse(final File file) throws IOException {
		visitor.visitBegin(file);
		@Cleanup final TextLines lines = TextLines.of(file).ignoringEmptyLines();
		for (final String line : lines) {
			if (line.startsWith("#")) {
				if (line.toUpperCase().startsWith("#EXTINF")) {
					final int indA = line.indexOf(",", 0);
					if (indA != -1) {
						visitor.visitTitle(line.substring(indA + 1, line.length()));
					}
					final int indB = line.indexOf(":", 0);
					if ((indB != -1) && (indB < indA)) {
						visitor.visitLength(Long.valueOf( (line.substring(indB + 1, indA)).trim()));
					}
				} else {
					visitor.visitComment(line);
				}
			} else {
				File f = new File(file.getParentFile(), line);
				if (!f.exists()) {
					f = new File(line);
				}
				visitor.visitFile(f);
			}
		}
		visitor.visitEnd(file);
	}
}
