#!/bin/bash
#
# semanticcms-tag-reference - Generates tag library descriptor documentation for .tld files.
# Copyright (C) 2020  AO Industries, Inc.
#     support@aoindustries.com
#     7262 Bull Pen Cir
#     Mobile, AL 36695
#
# This file is part of semanticcms-tag-reference.
#
# semanticcms-tag-reference is free software: you can redistribute it and/or modify
# it under the terms of the GNU Lesser General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# semanticcms-tag-reference is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public License
# along with semanticcms-tag-reference.  If not, see <http://www.gnu.org/licenses/>.
#

set -e
cd "${BASH_SOURCE%/*}"

# Note: This list matches ao-oss-parent/pom.xml
rm javadoc.link.javase.* -f
curl -fsS -o javadoc.link.javase.5 https://docs.oracle.com/javase/1.5.0/docs/api/package-list
curl -fsS -o javadoc.link.javase.6 https://docs.oracle.com/javase/6/docs/api/package-list
curl -fsS -o javadoc.link.javase.7 https://docs.oracle.com/javase/7/docs/api/package-list
curl -fsS -o javadoc.link.javase.8 https://docs.oracle.com/javase/8/docs/api/package-list
curl -fsS -o javadoc.link.javase.9 https://docs.oracle.com/javase/9/docs/api/package-list
curl -fsS -o javadoc.link.javase.10 https://docs.oracle.com/javase/10/docs/api/element-list
curl -fsS -o javadoc.link.javase.11 https://docs.oracle.com/en/java/javase/11/docs/api/element-list
curl -fsS -o javadoc.link.javase.12 https://docs.oracle.com/en/java/javase/12/docs/api/element-list
curl -fsS -o javadoc.link.javase.13 https://docs.oracle.com/en/java/javase/13/docs/api/element-list
curl -fsS -o javadoc.link.javase.14 https://docs.oracle.com/en/java/javase/14/docs/api/element-list
curl -fsS -o javadoc.link.javase.15 https://download.java.net/java/early_access/jdk15/docs/api/element-list

# Note: This list matches ao-oss-parent/pom.xml
rm javadoc.link.javamail -f
rm javadoc.link.javaee.* -f
curl -fsS -o javadoc.link.javamail https://javaee.github.io/javamail/docs/api/package-list
curl -fsS -o javadoc.link.javaee.5 https://docs.oracle.com/javaee/5/api/package-list
curl -fsS -o javadoc.link.javaee.6 https://docs.oracle.com/javaee/6/api/package-list
curl -fsS -o javadoc.link.javaee.7 https://docs.oracle.com/javaee/7/api/package-list
curl -fsS -o javadoc.link.javaee.8 https://javaee.github.io/javaee-spec/javadocs/package-list
