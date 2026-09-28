const API='/api';
const $=s=>document.querySelector(s);

function showMessage(text,error=false){const el=$('#message');el.textContent=text;el.className='message show'+(error?' error':'');setTimeout(()=>el.className='message',3500)}
async function api(url,options={}){const r=await fetch(url,{headers:{'Content-Type':'application/json',...(options.headers||{})},...options});let data=null;try{data=await r.json()}catch(e){}if(!r.ok){throw new Error(data?.message||'Request failed')}return data}

async function loadZones(){
  const zones=await api(`${API}/zones`);
  $('#zoneCount').textContent=zones.length;
  const options='<option value="">Select zone</option>'+zones.map(z=>`<option value="${z.id}">${z.code} - ${z.name}</option>`).join('');
  $('#householdZone').innerHTML=options;$('#scheduleZone').innerHTML=options;
  return zones;
}
async function loadHouseholds(){
  const households=await api(`${API}/households`);
  $('#householdCount').textContent=households.length;
  $('#flaggedCount').textContent=households.filter(h=>h.reminderFlagged).length;
  $('#pickupHousehold').innerHTML='<option value="">Select household</option>'+households.map(h=>`<option value="${h.id}">${h.houseNumber} - ${h.residentName} (${h.zoneCode})</option>`).join('');
  $('#householdTable').innerHTML=households.map(h=>`<tr><td>${h.houseNumber}</td><td>${h.residentName}</td><td>${h.zoneCode}</td><td>${Number(h.averageScore||0).toFixed(2)}</td><td class="status ${h.reminderFlagged?'flagged':'ok'}">${h.reminderFlagged?'Reminder Required':'Good'}</td></tr>`).join('')||'<tr><td colspan="5">No households yet.</td></tr>';
}
async function loadDashboard(){
  try{
    await loadZones();await loadHouseholds();
    const [pickups,scores]=await Promise.all([api(`${API}/pickups`),api(`${API}/pickups/reports/zone-scores`)]);
    $('#pickupCount').textContent=pickups.length;
    $('#zoneScores').innerHTML=scores.map(s=>`<tr><td>${s.zoneName}</td><td>${s.zoneCode}</td><td>${Number(s.averageScore||0).toFixed(2)}</td><td>${s.householdCount}</td><td>${s.flaggedHouseholdCount}</td></tr>`).join('')||'<tr><td colspan="5">No zones yet.</td></tr>';
  }catch(e){showMessage(e.message,true)}
}

$('#zoneForm').addEventListener('submit',async e=>{e.preventDefault();const f=new FormData(e.target);try{await api(`${API}/zones`,{method:'POST',body:JSON.stringify({name:f.get('name'),code:f.get('code'),active:true})});e.target.reset();showMessage('Zone added');loadDashboard()}catch(x){showMessage(x.message,true)}});
$('#householdForm').addEventListener('submit',async e=>{e.preventDefault();const f=new FormData(e.target);try{await api(`${API}/households`,{method:'POST',body:JSON.stringify({houseNumber:f.get('houseNumber'),residentName:f.get('residentName'),phone:f.get('phone'),zoneId:Number(f.get('zoneId'))})});e.target.reset();showMessage('Household added');loadDashboard()}catch(x){showMessage(x.message,true)}});
$('#scheduleForm').addEventListener('submit',async e=>{e.preventDefault();const f=new FormData(e.target);try{await api(`${API}/schedules`,{method:'POST',body:JSON.stringify({zoneId:Number(f.get('zoneId')),pickupDay:f.get('pickupDay'),startTime:f.get('startTime'),endTime:f.get('endTime')})});e.target.reset();showMessage('Schedule saved');loadDashboard()}catch(x){showMessage(x.message,true)}});
$('#pickupForm').addEventListener('submit',async e=>{e.preventDefault();const f=new FormData(e.target);try{await api(`${API}/pickups`,{method:'POST',body:JSON.stringify({householdId:Number(f.get('householdId')),collectorName:f.get('collectorName'),pickupTime:f.get('pickupTime')?f.get('pickupTime'):null,segregationScore:Number(f.get('segregationScore'))})});e.target.reset();showMessage('Pickup recorded and household score recalculated');loadDashboard()}catch(x){showMessage(x.message,true)}});

loadDashboard();
