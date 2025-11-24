<#--<!DOCTYPE html>-->
<#--<html>-->
<#--<head>-->
<#--    <title>Staff Overview</title>-->
<#--    <style>-->
<#--        body { font-family: Arial; margin: 20px; }-->
<#--        table { border-collapse: collapse; width: 100%; }-->
<#--        th, td { padding: 8px; border: 1px solid #bbb; }-->
<#--        th { background: #eee; }-->
<#--    </style>-->
<#--</head>-->
<#--<body>-->
<#--<h1>Staff Overview</h1>-->
<#--<form action="/staff/search" method="get">-->
<#--    <label for="staffId">Search by ID:</label>-->
<#--    <input type="text" name="id" id="staffId" placeholder="Enter staff ID">-->
<#--    <button type="submit">Search</button>-->
<#--</form>-->
<#--<table>-->
<#--    <thead>-->
<#--    <tr>-->
<#--        <th>id</th><th>Name</th><th>Nickname</th><th>Salary</th>-->
<#--    </tr>-->
<#--    </thead>-->
<#--    <tbody>-->
<#--    <#list staff as s>-->
<#--        <tr>-->
<#--            <td>${s.id}</td>-->
<#--            <td>${s.firstName} ${s.lastName}</td>-->
<#--            <td>${s.nickname}</td>-->
<#--            <td>-->
<#--                <#if s.salary??>-->
<#--                    ${s.salary.salary} ${s.salary.currency}-->
<#--                <#else>-->
<#--                    N/A-->
<#--                </#if>-->
<#--            </td>-->
<#--        </tr>-->
<#--    </#list>-->
<#--    </tbody>-->
<#--</table>-->

<#--</body>-->
<#--</html>-->

<!DOCTYPE html>
<html>
<head>
    <title>Staff Overview</title>
    <style>
        body {
            font-family: Arial, serif; margin: 20px; }
        table { border-collapse: collapse; width: 100%; margin-top: 20px; }
        th, td { padding: 8px; border: 1px solid #bbb; text-align: left; }
        th { background: #eee; }
        input, button { padding: 8px 12px; margin-top: 10px; margin-right: 5px; }
        button { cursor: pointer; background: #007bff; color: white; border: none; border-radius: 4px; }
        button:hover { background: #0056b3; }
        .search-container { margin-bottom: 20px; }
        .no-data { text-align: center; color: #666; font-style: italic; }
    </style>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
<h1>Staff Overview</h1>

<div class="search-container">
    <label for="searchId">Search by ID:</label>
    <input type="text" id="searchId" placeholder="Enter staff ID">
    <button id="searchBtn">Search</button>
    <button id="resetBtn">Reset</button>
</div>


<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Nickname</th>
        <th>Salary</th>
    </tr>
    </thead>
    <tbody id="staffTableBody">
    <#if staff?has_content>
        <#list staff as s>
            <tr>
                <td>${s.id}</td>
                <td>${s.firstName} ${s.lastName}</td>
                <td>${s.nickname!""}</td>
                <td>
                    <#if s.salary??>
                        ${s.salary.salary} ${s.salary.currency}
                    <#else>
                        N/A
                    </#if>
                </td>
            </tr>
        </#list>
    <#else>
        <tr>
            <td colspan="4" class="no-data">No staff members found</td>
        </tr>
    </#if>
    </tbody>
</table>

<script>
    $(document).ready(function() {

        $('#searchBtn').click(function() {
            const id = $('#searchId').val().trim();
            if (!id) {
                alert('Please enter a staff ID');
                return;
            }


            $.get('/api/staff/' + id)
                .done(function(data) {
                    renderStaff([data]);
                })
                .fail(function(xhr) {
                    if (xhr.status === 404) {
                        $('#staffTableBody').html('<tr><td colspan="4" class="no-data">Staff member not found</td></tr>');
                    } else {
                        $('#staffTableBody').html('<tr><td colspan="4" class="no-data">Error fetching staff data</td></tr>');
                    }
                });
        });

        // Reset
        $('#resetBtn').click(function() {
            $('#searchId').val('');


            $.get('/api/staff')
                .done(function(data) {
                    renderStaff(data);
                })
                .fail(function() {
                    $('#staffTableBody').html('<tr><td colspan="4" class="no-data">Error loading staff data</td></tr>');
                });
        });


        $('#searchId').keypress(function(e) {
            if (e.which === 13) { // Enter key
                $('#searchBtn').click();
            }
        });

        // Helper function to render staff data
        function renderStaff(staffArray) {
            if (!staffArray || staffArray.length === 0) {
                $('#staffTableBody').html('<tr><td colspan="4" class="no-data">No staff members found</td></tr>');
                return;
            }

            let rows = '';
            staffArray.forEach(function(s) {
                const salaryDisplay = s.salary ?
                    s.salary.salary + ' ' + s.salary.currency :
                    'N/A';

                rows += '<tr>' +
                    '<td>' + escapeHtml(s.id) + '</td>' +
                    '<td>' + escapeHtml(s.firstName) + ' ' + escapeHtml(s.lastName) + '</td>' +
                    '<td>' + escapeHtml(s.nickname || '') + '</td>' +
                    '<td>' + salaryDisplay + '</td>' +
                    '</tr>';
            });
            $('#staffTableBody').html(rows);
        }

        // Helper function to escape HTML (prevent XSS)
        function escapeHtml(text) {
            if (!text) return '';
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        }
    });
</script>

</body>
</html>