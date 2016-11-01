<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="text" />
    
    <xsl:template match="/ead">
      <xsl:text>&quot;</xsl:text>
      <xsl:value-of select="//unitid"/>
      <xsl:text>&quot;</xsl:text>
      <xsl:text>,</xsl:text>
      
      <xsl:text>&quot;</xsl:text>
      <xsl:variable name="q">"</xsl:variable>
      <xsl:variable name="a">'</xsl:variable>
      <xsl:value-of select="translate(//titlestmt/titleproper/text(),$q,$a)"/>
      <xsl:text>&quot;</xsl:text>
      <xsl:text>,</xsl:text>
      
      <xsl:text>&quot;</xsl:text>
      <xsl:choose>
        <xsl:when test="//unitdate[@type='inclusive']">
          <xsl:value-of select="//unitdate[@type='inclusive']"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="//unitdate/text()"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:text>&quot;</xsl:text>
      <xsl:text>,</xsl:text>

      <xsl:text>&quot;</xsl:text>
      <xsl:for-each select="//controlaccess/subject|//controlaccess/genreform|//controlaccess/*">
        <xsl:value-of select="text()"/>
        <xsl:text>; </xsl:text>
      </xsl:for-each>
      <xsl:text>&quot;</xsl:text>
    </xsl:template>
   
</xsl:stylesheet>
