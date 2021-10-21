package projekti.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Image implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Account account;

    private String name;
    private String contentType;
    private Long contentLength;
    private String text;

    private boolean profilePic;

    @ManyToMany
    @JoinTable(uniqueConstraints = @UniqueConstraint(columnNames = {"image_id", "likes_id"}))
    private List<Account> likes = new ArrayList<>();

    @OneToMany
    private List<Comment> comments = new ArrayList<>();

    @Type(type = "org.hibernate.type.ImageType")
    @Basic(fetch = FetchType.LAZY)
    private byte[] content;

    public List<Comment> getPagedComments(int page) {
        if (comments.size()>=10){
            List<List<Comment>> smallerLists = Lists.partition(comments, 10);
            return smallerLists.get(page);
        }
        return comments;
    }

}
