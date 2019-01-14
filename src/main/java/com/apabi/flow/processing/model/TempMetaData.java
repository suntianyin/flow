package com.apabi.flow.processing.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class TempMetaData {

    private String metaId;

    private String idType;

    private String saleStatus;

    private String language;

    private String title;

    private String subTitle;

    private String creator;

    private String creatorWord;

    private String creatorId;

    private String contributor;

    private String contributorWord;

    private String contributorId;

    private String translator;

    private String translatorId;

    private String originTitle;

    private String alternativeTitle;

    private String editionOrder;

    private String editionNote;

    private String place;

    private String publisher;

    private String publisherId;

    private String issuedDate;

    private String endIssuedDate;

    private String issuedDateDesc;

    private String subject;

    private String reader;

    private String classCode;

    private String apabiClass;

    private String type;

    private String isbn;

    private String isbn10;

    private String isbn13;

    private String paperPrice;

    private String ebookPrice;

    private String foreignPrice;

    private String foreignPriceType;

    private String paperPriceDesc;

    private String binding;

    private String illustration;

    private String pressOrder;

    private String editor;

    private String relation;

    private String relationId;

    private String volume;

    private String isSeries;

    private String volumesCount;

    private String isAllublished;

    private String notes;

    private String podPrice;

    private String podPriceType;

    private String podPricePageUnit;

    private String drId;

    private String doubanId;

    private String amazonId;

    private String calisId;

    private String nlibraryId;

    private String dataSource;

    private String qualityRating;

    private String qrType;

    private String cebxObjId;

    private String cebxFileSize;

    private Integer hasCebx;

    private String cebxPage;

    private String complexOid;

    private String reditor;

    private String departmentId;

    private String coverUrl;

    private String imgHeigth;

    private String imgWidth;

    private String covObjId;

    private String thumImgUrl;

    private String thumImgSize;

    private String mediumCover;

    private Integer hasFlow;

    private Integer isOptimize;

    private String donor;

    private String libraryId;

    private String bookId;

    private String styleClass;

    private String chapterNum;

    private String bookPages;

    private String tags;

    private String createTime;

    private String updateTime;

    private String contentNum;

//    private List<StreamCatalog> foamatCatalog;

//    private List<StreamCatalog> streamCatalog;

    private String hasPublish;

    private String isPublicCopyRight;

    private String relationType;

    private String authorIntro;

    private String abstract_;

    private String preface;

    private String styleUrl;

    private String postScript;

    private String isReadEpub;

    private String flowSource;

    private String isReadCebxFlow;

    private String hasProcessingFile;

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

    public String getSaleStatus() {
        return saleStatus;
    }

    public void setSaleStatus(String saleStatus) {
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public String getIsSeries() {
        return isSeries;
    }

    public void setIsSeries(String isSeries) {
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

    public String getDrId() {
        return drId;
    }

    public void setDrId(String drId) {
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

    public String getChapterNum() {
        return chapterNum;
    }

    public void setChapterNum(String chapterNum) {
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getContentNum() {
        return contentNum;
    }

    public void setContentNum(String contentNum) {
        this.contentNum = contentNum;
    }

//    public List<StreamCatalog> getFoamatCatalog() {
//        return foamatCatalog;
//    }
//
//    public void setFoamatCatalog(List<StreamCatalog> foamatCatalog) {
//        this.foamatCatalog = foamatCatalog;
//    }
//
//    public List<StreamCatalog> getStreamCatalog() {
//        return streamCatalog;
//    }
//
//    public void setStreamCatalog(List<StreamCatalog> streamCatalog) {
//        this.streamCatalog = streamCatalog;
//    }

    public String getHasPublish() {
        return hasPublish;
    }

    public void setHasPublish(String hasPublish) {
        this.hasPublish = hasPublish;
    }

    public String getIsPublicCopyRight() {
        return isPublicCopyRight;
    }

    public void setIsPublicCopyRight(String isPublicCopyRight) {
        this.isPublicCopyRight = isPublicCopyRight;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public String getAuthorIntro() {
        return authorIntro;
    }

    public void setAuthorIntro(String authorIntro) {
        this.authorIntro = authorIntro;
    }

    public String getAbstract_() {
        return abstract_;
    }

    public void setAbstract_(String abstract_) {
        this.abstract_ = abstract_;
    }

    public String getPreface() {
        return preface;
    }

    public void setPreface(String preface) {
        this.preface = preface;
    }

    public String getStyleUrl() {
        return styleUrl;
    }

    public void setStyleUrl(String styleUrl) {
        this.styleUrl = styleUrl;
    }

    public String getPostScript() {
        return postScript;
    }

    public void setPostScript(String postScript) {
        this.postScript = postScript;
    }

    public String getIsReadEpub() {
        return isReadEpub;
    }

    public void setIsReadEpub(String isReadEpub) {
        this.isReadEpub = isReadEpub;
    }

    public String getFlowSource() {
        return flowSource;
    }

    public void setFlowSource(String flowSource) {
        this.flowSource = flowSource;
    }

    public String getIsReadCebxFlow() {
        return isReadCebxFlow;
    }

    public void setIsReadCebxFlow(String isReadCebxFlow) {
        this.isReadCebxFlow = isReadCebxFlow;
    }

    public String getHasProcessingFile() {
        return hasProcessingFile;
    }

    public void setHasProcessingFile(String hasProcessingFile) {
        this.hasProcessingFile = hasProcessingFile;
    }


    class StreamCatalog{
        private String chapterName;
        private Integer chapterNum;
        private List<StreamCatalog> children;
        private Integer ebookPageNum;
        private Integer wordSum;
        private String url;

        public String getChapterName() {
            return chapterName;
        }

        public void setChapterName(String chapterName) {
            this.chapterName = chapterName;
        }

        public Integer getChapterNum() {
            return chapterNum;
        }

        public void setChapterNum(Integer chapterNum) {
            this.chapterNum = chapterNum;
        }

        public List<StreamCatalog> getChildren() {
            return children;
        }

        public void setChildren(List<StreamCatalog> children) {
            this.children = children;
        }

        public Integer getEbookPageNum() {
            return ebookPageNum;
        }

        public void setEbookPageNum(Integer ebookPageNum) {
            this.ebookPageNum = ebookPageNum;
        }

        public Integer getWordSum() {
            return wordSum;
        }

        public void setWordSum(Integer wordSum) {
            this.wordSum = wordSum;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}