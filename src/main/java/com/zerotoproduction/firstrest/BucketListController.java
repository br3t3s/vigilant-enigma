package com.zerotoproduction.firstrest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RestController
public class BucketListController {

    @Autowired
    private JavaMailSender javaMailSender;

    private List<BucketList> myBucketList = new ArrayList();
    private List<String> requests = new ArrayList();
    private List<String> headers = new ArrayList();
    private final AtomicLong counter = new AtomicLong();

    public BucketListController() {
        myBucketList.add(new BucketList(counter.incrementAndGet(), "Visit Colosseum in Rome"));
    }

    @GetMapping(value = "/")
    public ResponseEntity index() {
        return ResponseEntity.ok(myBucketList);
    }

    @GetMapping(value = "/bucket")
    public ResponseEntity getBucket(@RequestParam(value = "id") Long id) {
        BucketList itemToReturn = null;
        for (BucketList bucket : myBucketList) {
            if (bucket.getId() == id)
                itemToReturn = bucket;
        }

        if (itemToReturn == null) {
            return ResponseEntity.ok("No bucket found with id " + id);
        } else {
            return ResponseEntity.ok(itemToReturn);
        }

    }

    @GetMapping(path = "/bbb")
    public ResponseEntity createBBB() {
        return ResponseEntity.ok(requests);
    }

    @GetMapping(path = "/ccc")
    public ResponseEntity createccc() {
        return ResponseEntity.ok(headers);
    }



    @PostMapping(path = "/abc")
    public ResponseEntity createAbc(HttpServletRequest request) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader in = request.getReader()) {
            char[] buf = new char[4096];
            for (int len; (len = in.read(buf)) > 0; )
                builder.append(buf, 0, len);
        }
        String requestBody = builder.toString();

        List<String> collect = enumerationAsStream(request.getHeaderNames())
                .map(request::getHeader)
                .collect(Collectors.toList());

        headers.addAll(collect);

        requests.add(requestBody);
        return ResponseEntity.ok(requestBody);
    }

    public static <T> Stream<T> enumerationAsStream(Enumeration<T> e) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        new Iterator<T>() {
                            public T next() {
                                return e.nextElement();
                            }
                            public boolean hasNext() {
                                return e.hasMoreElements();
                            }
                        },
                        Spliterator.ORDERED), false);
    }

    @PostMapping(value = "/")
    public ResponseEntity addToBucketList(@RequestParam(value = "name") String name) {
        myBucketList.add(new BucketList(counter.incrementAndGet(), name));
        return ResponseEntity.ok(myBucketList);
    }

    @PutMapping(value = "/")
    public ResponseEntity updateBucketList(@RequestParam(value = "name") String name, @RequestParam(value = "id") Long id) {
        myBucketList.forEach(bucketList -> {
            if (bucketList.getId() == id) {
                bucketList.setName(name);
            }
        });
        return ResponseEntity.ok(myBucketList);
    }

    @DeleteMapping(value = "/")
    public ResponseEntity removeBucketList(@RequestParam(value = "id") Long id) {
        BucketList itemToRemove = null;
        for (BucketList bucket : myBucketList) {
            if (bucket.getId() == id)
                itemToRemove = bucket;
        }

        myBucketList.remove(itemToRemove);
        return ResponseEntity.ok(myBucketList);
    }

    public void sendCreateEventNotificationToUsers(String text) {
        try {
            System.out.println("DDDDDDDDDDDDDDDD");
            javaMailSender.send(createMessage(text));
        } catch (Exception e) {
            System.out.println("DDDDDDDDDDDDDDDD " + e.getMessage());
        }
    }


    private MimeMessage createMessage(String text) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setSubject("test webhook");
        mimeMessageHelper.setFrom(new InternetAddress("dirtysparrows.mailer@gmail.com", "Trenuj z Nixonem"));
        mimeMessageHelper.setTo("br3t3s@gmail.com");
        mimeMessageHelper.setText(text, true);
        return mimeMessageHelper.getMimeMessage();
    }
}
