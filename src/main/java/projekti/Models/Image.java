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
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private List<Account> likes = new ArrayList<>();

    @OneToMany
    private List<Comment> comments = new ArrayList<>();

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] content;

}
