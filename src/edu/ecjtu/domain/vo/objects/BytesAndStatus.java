package edu.ecjtu.domain.vo.objects;

public class BytesAndStatus {
	private Long downloadId;
	private Integer downSize;
	private Integer totalSize;
	private Integer downloadStatus;
	private Double netSpeed;
	private String Title;

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public Long getDownloadId() {
		return downloadId;
	}

	public void setDownloadId(Long downloadId) {
		this.downloadId = downloadId;
	}

	public Double getNetSpeed() {
		return netSpeed;
	}

	public void setNetSpeed(Double netSpeed) {
		this.netSpeed = netSpeed;
	}

	public Integer getDownSize() {
		return downSize;
	}

	public void setDownSize(Integer downSize) {
		this.downSize = downSize;
	}

	public Integer getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(Integer totalSize) {
		this.totalSize = totalSize;
	}

	public Integer getDownloadStatus() {
		return downloadStatus;
	}

	public void setDownloadStatus(Integer downloadStatus) {
		this.downloadStatus = downloadStatus;
	}

	public BytesAndStatus() {
		super();
	}

	@Override
	public String toString() {
		return "BytesAndStatus [downloadId=" + downloadId + ", downSize="
				+ downSize + ", totalSize=" + totalSize + ", downloadStatus="
				+ downloadStatus + ", netSpeed=" + netSpeed + ", Title="
				+ Title + "]";
	}
}
