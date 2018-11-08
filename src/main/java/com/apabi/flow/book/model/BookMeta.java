package com.apabi.flow.book.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author guanpp
 * @date 2018/8/1 9:52
 * @description
 */
@Table(name = "APABI_BOOK_METADATA")
@Entity
public class BookMeta implements Serializable{
    @Id
    @Column(name = "METAID")
    private String metaId;

    @Column(name = "IDTYPE")
    private String idType;

    @Column(name = "SALESTATUS")
    private Integer saleStatus;

    @Column(name = "LANGUAGE")
    private String language;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "SUBTITLE")
    private String subTitle;

    @Column(name = "CREATOR")
    private String creator;

    @Column(name = "AUTHORINTRO")
    private String authorIntro;

    @Column(name = "CREATORWORD")
    private String creatorWord;

    @Column(name = "CREATORID")
    private String creatorId;

    @Column(name = "CONTRIBUTOR")
    private String contributor;

    @Column(name = "CONTRIBUTORWORD")
    private String contributorWord;

    @Column(name = "CONTRIBUTORID")
    private String contributorId;

    @Column(name = "TRANSLATOR")
    private String translator;

    @Column(name = "TRANSLATORID")
    private String translatorId;

    @Column(name = "ORIGINTITLE")
    private String originTitle;

    @Column(name = "ALTERNATIVETITLE")
    private String alternativeTitle;

    @Column(name = "EDITIONORDER")
    private String editionOrder;

    @Column(name = "EDITIONNOTE")
    private String editionNote;

    @Column(name = "PLACE")
    private String place;

    @Column(name = "PUBLISHER")
    private String publisher;

    @Column(name = "PUBLISHERID")
    private String publisherId;

    @Column(name = "ISSUEDDATE")
    private String issuedDate;

    @Column(name = "ENDISSUEDDATE")
    private String endIssuedDate;

    @Column(name = "ISSUEDDATEDESC")
    private String issuedDateDesc;

    @Column(name = "ABSTRACT")
    private String abstract_;

    @Column(name = "SUBJECT")
    private String subject;

    @Column(name = "PREFACE")
    private String preface;

    @Column(name = "READER")
    private String reader;

    @Column(name = "CLASSCODE")
    private String classCode;

    @Column(name = "APABICLASS")
    private String apabiClass;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "ISBN")
    private String isbn;

    @Column(name = "ISBN10")
    private String isbn10;

    @Column(name = "ISBN13")
    private String isbn13;

    @Column(name = "PAPERPRICE")
    private String paperPrice;

    @Column(name = "EBOOKPRICE")
    private String ebookPrice;

    @Column(name = "FOREIGNPRICE")
    private String foreignPrice;

    @Column(name = "FOREIGNPRICETYPE")
    private String foreignPriceType;

    @Column(name = "PAPERPRICEDESC")
    private String paperPriceDesc;

    @Column(name = "BINDING")
    private String binding;

    @Column(name = "ILLUSTRATION")
    private String illustration;

    @Column(name = "PRESSORDER")
    private String pressOrder;

    @Column(name = "EDITOR")
    private String editor;

    @Column(name = "RELATION")
    private String relation;

    @Column(name = "RELATIONID")
    private String relationId;

    @Column(name = "VOLUME")
    private String volume;

    @Column(name = "ISSERIES")
    private Integer isSeries;

    @Column(name = "VOLUMESCOUNT")
    private String volumesCount;

    @Column(name = "ISALLUBLISHED")
    private String isAllublished;

    @Column(name = "NOTES")
    private String notes;

    @Column(name = "PODPRICE")
    private String podPrice;

    @Column(name = "PODPRICETYPE")
    private String podPriceType;

    @Column(name = "PODPRICEPAGEUNIT")
    private String podPricePageUnit;

    @Column(name = "DRID")
    private Integer drId;

    @Column(name = "DOUBANID")
    private String doubanId;

    @Column(name = "AMAZONID")
    private String amazonId;

    @Column(name = "CALISID")
    private String calisId;

    @Column(name = "NLIBRARYID")
    private String nlibraryId;

    @Column(name = "DATASOURCE")
    private String dataSource;

    @Column(name = "QUALITYRATING")
    private String qualityRating;

    @Column(name = "QRTYPE")
    private String qrType;

    @Column(name = "CEBXOBJID")
    private String cebxObjId;

    @Column(name = "CEBXFILESIZE")
    private String cebxFileSize;

    @Column(name = "HASCEBX")
    private Integer hasCebx;

    @Column(name = "CEBXPAGE")
    private String cebxPage;

    @Column(name = "COMPLEXOID")
    private String complexOid;

    @Column(name = "REDITOR")
    private String reditor;

    @Column(name = "DEPARTMENTID")
    private String departmentId;

    @Column(name = "COVERURL")
    private String coverUrl;

    @Column(name = "IMGHEIGTH")
    private String imgHeigth;

    @Column(name = "IMGWIDTH")
    private String imgWidth;

    @Column(name = "COVOBJID")
    private String covObjId;

    @Column(name = "THUMIMGURL")
    private String thumImgUrl;

    @Column(name = "THUMIMGSIZE")
    private String thumImgSize;

    @Column(name = "MEDIUMCOVER")
    private String mediumCover;

    @Column(name = "HASFLOW")
    private Integer hasFlow;

    @Column(name = "ISOPTIMIZE")
    private Integer isOptimize;

    @Column(name = "DONOR")
    private String donor;

    @Column(name = "LIBRARYID")
    private String libraryId;

    @Column(name = "BOOKID")
    private String bookId;

    @Column(name = "STYLECLASS")
    private String styleClass;

    @Column(name = "STYLEURL")
    private String styleUrl;

    @Column(name = "CHAPTERNUM")
    private Integer chapterNum;

    @Column(name = "BOOKPAGES")
    private String bookPages;

    @Column(name = "TAGS")
    private String tags;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    @Column(name = "CREATETIME")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    @Column(name = "UPDATETIME")
    private Date updateTime;

    @Column(name = "CONTENTNUM")
    private Integer contentNum;

    @Column(name = "FOAMATCATALOG")
    private String foamatCatalog;

    @Column(name = "STREAMCATALOG")
    private String streamCatalog;

    @Column(name = "HASPUBLISH")
    private Integer hasPublish;

    @Column(name = "ISPUBLICCOPYRIGHT")
    private Integer isPublicCopyRight;

    @Column(name = "FLOWSOURCE")
    private String flowSource;

    @Column(name = "ISREADCEBXFLOW")
    private Integer isReadCebxFlow;

    @Column(name = "ISREADEPUB")
    private Integer isReadEpub;

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public Integer getSaleStatus() {
        return saleStatus;
    }

    public void setSaleStatus(Integer saleStatus) {
        this.saleStatus = saleStatus;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getAuthorIntro() {
        return authorIntro;
    }

    public void setAuthorIntro(String authorIntro) {
        this.authorIntro = authorIntro;
    }

    public String getCreatorWord() {
        return creatorWord;
    }

    public void setCreatorWord(String creatorWord) {
        this.creatorWord = creatorWord;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getContributorWord() {
        return contributorWord;
    }

    public void setContributorWord(String contributorWord) {
        this.contributorWord = contributorWord;
    }

    public String getContributorId() {
        return contributorId;
    }

    public void setContributorId(String contributorId) {
        this.contributorId = contributorId;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public String getTranslatorId() {
        return translatorId;
    }

    public void setTranslatorId(String translatorId) {
        this.translatorId = translatorId;
    }

    public String getOriginTitle() {
        return originTitle;
    }

    public void setOriginTitle(String originTitle) {
        this.originTitle = originTitle;
    }

    public String getAlternativeTitle() {
        return alternativeTitle;
    }

    public void setAlternativeTitle(String alternativeTitle) {
        this.alternativeTitle = alternativeTitle;
    }

    public String getEditionOrder() {
        return editionOrder;
    }

    public void setEditionOrder(String editionOrder) {
        this.editionOrder = editionOrder;
    }

    public String getEditionNote() {
        return editionNote;
    }

    public void setEditionNote(String editionNote) {
        this.editionNote = editionNote;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getEndIssuedDate() {
        return endIssuedDate;
    }

    public void setEndIssuedDate(String endIssuedDate) {
        this.endIssuedDate = endIssuedDate;
    }

    public String getIssuedDateDesc() {
        return issuedDateDesc;
    }

    public void setIssuedDateDesc(String issuedDateDesc) {
        this.issuedDateDesc = issuedDateDesc;
    }

    public String getAbstract_() {
        return abstract_;
    }

    public void setAbstract_(String abstract_) {
        this.abstract_ = abstract_;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPreface() {
        return preface;
    }

    public void setPreface(String preface) {
        this.preface = preface;
    }

    public String getReader() {
        return reader;
    }

    public void setReader(String reader) {
        this.reader = reader;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getApabiClass() {
        return apabiClass;
    }

    public void setApabiClass(String apabiClass) {
        this.apabiClass = apabiClass;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn10() {
        return isbn10;
    }

    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getPaperPrice() {
        return paperPrice;
    }

    public void setPaperPrice(String paperPrice) {
        this.paperPrice = paperPrice;
    }

    public String getEbookPrice() {
        return ebookPrice;
    }

    public void setEbookPrice(String ebookPrice) {
        this.ebookPrice = ebookPrice;
    }

    public String getForeignPrice() {
        return foreignPrice;
    }

    public void setForeignPrice(String foreignPrice) {
        this.foreignPrice = foreignPrice;
    }

    public String getForeignPriceType() {
        return foreignPriceType;
    }

    public void setForeignPriceType(String foreignPriceType) {
        this.foreignPriceType = foreignPriceType;
    }

    public String getPaperPriceDesc() {
        return paperPriceDesc;
    }

    public void setPaperPriceDesc(String paperPriceDesc) {
        this.paperPriceDesc = paperPriceDesc;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getIllustration() {
        return illustration;
    }

    public void setIllustration(String illustration) {
        this.illustration = illustration;
    }

    public String getPressOrder() {
        return pressOrder;
    }

    public void setPressOrder(String pressOrder) {
        this.pressOrder = pressOrder;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getRelationId() {
        return relationId;
    }

    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public Integer getIsSeries() {
        return isSeries;
    }

    public void setIsSeries(Integer isSeries) {
        this.isSeries = isSeries;
    }

    public String getVolumesCount() {
        return volumesCount;
    }

    public void setVolumesCount(String volumesCount) {
        this.volumesCount = volumesCount;
    }

    public String getIsAllublished() {
        return isAllublished;
    }

    public void setIsAllublished(String isAllublished) {
        this.isAllublished = isAllublished;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPodPrice() {
        return podPrice;
    }

    public void setPodPrice(String podPrice) {
        this.podPrice = podPrice;
    }

    public String getPodPriceType() {
        return podPriceType;
    }

    public void setPodPriceType(String podPriceType) {
        this.podPriceType = podPriceType;
    }

    public String getPodPricePageUnit() {
        return podPricePageUnit;
    }

    public void setPodPricePageUnit(String podPricePageUnit) {
        this.podPricePageUnit = podPricePageUnit;
    }

    public Integer getDrId() {
        return drId;
    }

    public void setDrId(Integer drId) {
        this.drId = drId;
    }

    public String getDoubanId() {
        return doubanId;
    }

    public void setDoubanId(String doubanId) {
        this.doubanId = doubanId;
    }

    public String getAmazonId() {
        return amazonId;
    }

    public void setAmazonId(String amazonId) {
        this.amazonId = amazonId;
    }

    public String getCalisId() {
        return calisId;
    }

    public void setCalisId(String calisId) {
        this.calisId = calisId;
    }

    public String getNlibraryId() {
        return nlibraryId;
    }

    public void setNlibraryId(String nlibraryId) {
        this.nlibraryId = nlibraryId;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getQualityRating() {
        return qualityRating;
    }

    public void setQualityRating(String qualityRating) {
        this.qualityRating = qualityRating;
    }

    public String getQrType() {
        return qrType;
    }

    public void setQrType(String qrType) {
        this.qrType = qrType;
    }

    public String getCebxObjId() {
        return cebxObjId;
    }

    public void setCebxObjId(String cebxObjId) {
        this.cebxObjId = cebxObjId;
    }

    public String getCebxFileSize() {
        return cebxFileSize;
    }

    public void setCebxFileSize(String cebxFileSize) {
        this.cebxFileSize = cebxFileSize;
    }

    public Integer getHasCebx() {
        return hasCebx;
    }

    public void setHasCebx(Integer hasCebx) {
        this.hasCebx = hasCebx;
    }

    public String getCebxPage() {
        return cebxPage;
    }

    public void setCebxPage(String cebxPage) {
        this.cebxPage = cebxPage;
    }

    public String getComplexOid() {
        return complexOid;
    }

    public void setComplexOid(String complexOid) {
        this.complexOid = complexOid;
    }

    public String getReditor() {
        return reditor;
    }

    public void setReditor(String reditor) {
        this.reditor = reditor;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getImgHeigth() {
        return imgHeigth;
    }

    public void setImgHeigth(String imgHeigth) {
        this.imgHeigth = imgHeigth;
    }

    public String getImgWidth() {
        return imgWidth;
    }

    public void setImgWidth(String imgWidth) {
        this.imgWidth = imgWidth;
    }

    public String getCovObjId() {
        return covObjId;
    }

    public void setCovObjId(String covObjId) {
        this.covObjId = covObjId;
    }

    public String getThumImgUrl() {
        return thumImgUrl;
    }

    public void setThumImgUrl(String thumImgUrl) {
        this.thumImgUrl = thumImgUrl;
    }

    public String getThumImgSize() {
        return thumImgSize;
    }

    public void setThumImgSize(String thumImgSize) {
        this.thumImgSize = thumImgSize;
    }

    public String getMediumCover() {
        return mediumCover;
    }

    public void setMediumCover(String mediumCover) {
        this.mediumCover = mediumCover;
    }

    public Integer getHasFlow() {
        return hasFlow;
    }

    public void setHasFlow(Integer hasFlow) {
        this.hasFlow = hasFlow;
    }

    public Integer getIsOptimize() {
        return isOptimize;
    }

    public void setIsOptimize(Integer isOptimize) {
        this.isOptimize = isOptimize;
    }

    public String getDonor() {
        return donor;
    }

    public void setDonor(String donor) {
        this.donor = donor;
    }

    public String getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(String libraryId) {
        this.libraryId = libraryId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getStyleUrl() {
        return styleUrl;
    }

    public void setStyleUrl(String styleUrl) {
        this.styleUrl = styleUrl;
    }

    public Integer getChapterNum() {
        return chapterNum;
    }

    public void setChapterNum(Integer chapterNum) {
        this.chapterNum = chapterNum;
    }

    public String getBookPages() {
        return bookPages;
    }

    public void setBookPages(String bookPages) {
        this.bookPages = bookPages;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getContentNum() {
        return contentNum;
    }

    public void setContentNum(Integer contentNum) {
        this.contentNum = contentNum;
    }

    public String getFoamatCatalog() {
        return foamatCatalog;
    }

    public void setFoamatCatalog(String foamatCatalog) {
        this.foamatCatalog = foamatCatalog;
    }

    public String getStreamCatalog() {
        return streamCatalog;
    }

    public void setStreamCatalog(String streamCatalog) {
        this.streamCatalog = streamCatalog;
    }

    public Integer getHasPublish() {
        return hasPublish;
    }

    public void setHasPublish(Integer hasPublish) {
        this.hasPublish = hasPublish;
    }

    public Integer getIsPublicCopyRight() {
        return isPublicCopyRight;
    }

    public void setIsPublicCopyRight(Integer isPublicCopyRight) {
        this.isPublicCopyRight = isPublicCopyRight;
    }

    public String getFlowSource() {
        return flowSource;
    }

    public void setFlowSource(String flowSource) {
        this.flowSource = flowSource;
    }

    public Integer getIsReadCebxFlow() {
        return isReadCebxFlow;
    }

    public void setIsReadCebxFlow(Integer isReadCebxFlow) {
        this.isReadCebxFlow = isReadCebxFlow;
    }

    public Integer getIsReadEpub() {
        return isReadEpub;
    }

    public void setIsReadEpub(Integer isReadEpub) {
        this.isReadEpub = isReadEpub;
    }
}
