<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0" xmlns:marc="http://www.loc.gov/MARC21/slim" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

    <xsl:template match="/*">
        <results>
            <xsl:apply-templates select="marc:record"/>
        </results>
    </xsl:template>

    <xsl:template match="marc:record">
        <result>
            <xsl:attribute name="title">
                <xsl:value-of select="marc:datafield[@tag='245']/marc:subfield[@code='a']/text()"/>
            </xsl:attribute>
            <xsl:attribute name="author">
                <xsl:value-of select="marc:datafield[@tag='100']/marc:subfield[@code='a']/text()"/>
            </xsl:attribute>
            <xsl:attribute name="f949">
                <xsl:value-of select="count(marc:datafield[@tag='980'])"/>
            </xsl:attribute>
            <xsl:attribute name="f980">
                <xsl:value-of select="count(marc:datafield[@tag='980'])"/>
            </xsl:attribute>
            <xsl:attribute name="f981">
                <xsl:value-of select="count(marc:datafield[@tag='981'])"/>
            </xsl:attribute>
            <xsl:attribute name="f935">
                <xsl:value-of select="count(marc:datafield[@tag='935'])"/>
            </xsl:attribute>
            <xsl:for-each select="marc:datafield[@tag='949']">
                <xsl:attribute name="f949i">
                    <xsl:value-of select="marc:subfield[@code='i']/text()"/>
                </xsl:attribute>
                <xsl:attribute name="f949l">
                    <xsl:value-of select="marc:subfield[@code='l']/text()"/>
                </xsl:attribute>
                <xsl:attribute name="f949s">
                    <xsl:value-of select="marc:subfield[@code='s']/text()"/>
                </xsl:attribute>
                <xsl:attribute name="f949t">
                    <xsl:value-of select="marc:subfield[@code='t']/text()"/>
                </xsl:attribute>
                <xsl:attribute name="f949z">
                    <xsl:value-of select="marc:subfield[@code='z']/text()"/>
                </xsl:attribute>
                <xsl:attribute name="f949a">
                    <xsl:value-of select="marc:subfield[@code='a']/text()"/>
                </xsl:attribute>
                <xsl:attribute name="f949b">
                    <xsl:value-of select="marc:subfield[@code='b']/text()"/>
                </xsl:attribute>
                <xsl:attribute name="f949err">
                    <xsl:if test="string-length(marc:subfield[@code='i']/text())!=14">
                        <xsl:text>949i Must be 14 characters. </xsl:text>
                    </xsl:if>
                    <xsl:if test="not(starts-with(marc:subfield[@code='i']/text(), '39020'))">
                        <xsl:text>949i must start with 39020. </xsl:text>
                    </xsl:if>
                    <xsl:if test="not(marc:subfield[@code='z'][text()='090'])">
                        <xsl:text>949z must be 090. </xsl:text>
                    </xsl:if>
                    <!-- 
                    Cannot test sequence in MarcXML
                    <xsl:if test="marc:subfield[@code='a'] and not(marc:subfield[@code='a'][preceding-sibling::marc:subfield[@code='z']])">
                        <xsl:text>949a must be preceded by 949z. </xsl:text>
                    </xsl:if>
                    <xsl:if test="marc:subfield[@code='b'] and not(marc:subfield[@code='b'][preceding-sibling::marc:subfield[@code='z']])">
                        <xsl:text>949b must be preceded by 949z. </xsl:text>
                    </xsl:if>
                     -->
                </xsl:attribute>
            </xsl:for-each>
            <xsl:for-each select="marc:datafield[@tag='980']">
                <xsl:attribute name="f980err">
                    <xsl:if test="count(marc:subfield[@code='b']) &gt; 1">
                        <xsl:value-of select="concat(count(marc:subfield[@code='b']), ' instances of 980b. ')"/>
                    </xsl:if>
                    <xsl:if test="count(marc:subfield[@code='e']) &gt; 1">
                        <xsl:value-of select="concat(count(marc:subfield[@code='e']), ' instances of 980e. ')"/>
                    </xsl:if>
                    <xsl:if test="count(marc:subfield[@code='f']) &gt; 1">
                        <xsl:value-of select="concat(count(marc:subfield[@code='f']), ' instances of 980f. ')"/>
                    </xsl:if>
                </xsl:attribute>
            </xsl:for-each>
            <xsl:for-each select="marc:datafield[@tag='981']">
                <xsl:attribute name="f981err">
                    <xsl:if test="not(marc:subfield[@code='b'])">
                        <xsl:text>981b must exist. </xsl:text>
                    </xsl:if>
                </xsl:attribute>
                <xsl:attribute name="f981b">
                    <xsl:value-of select="marc:subfield[@code='b']/text()"/>
                </xsl:attribute>
            </xsl:for-each>
            <xsl:for-each select="marc:datafield[@tag='935']">
                <xsl:attribute name="f935err">
                    <xsl:if test="not(marc:subfield[@code='a'][starts-with(text(),'.o')])">
                        <xsl:text>935a must exist and start with '.o'. </xsl:text>
                    </xsl:if>
                </xsl:attribute>
                <xsl:attribute name="f935a">
                    <xsl:value-of select="marc:subfield[@code='a']/text()"/>
                </xsl:attribute>
            </xsl:for-each>
        </result>
    </xsl:template>

</xsl:stylesheet>
